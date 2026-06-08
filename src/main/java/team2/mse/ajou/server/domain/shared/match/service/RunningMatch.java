package team2.mse.ajou.server.domain.shared.match.service;

import lombok.Getter;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.states.IMatchStateLogic;
import team2.mse.ajou.server.domain.shared.observer.FlowMappedObservable;
import team2.mse.ajou.server.domain.shared.observer.Observable;
import team2.mse.ajou.server.domain.shared.observer.Observer;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * 매치별 내부 데이터 (게임과 연관없는 데이터) 관리용 클래스.
 * 아래 데이터를 관리합니다.
 * - 현재 State와 대응하는 State 구현
 * - 매치에서 리스닝 중인 `Observable`들 (플레이어)
 * - 타이머 기능이 필요할 때를 대비한 `ScheduledFuture`
 *
 * @author Ahn Yubin / 202021088
 */
public class RunningMatch {
    @Getter
    private UUID matchId;
    private ScheduledFuture<?> timerHandle;

    private MATCH_STATE currentState;
    private IMatchStateLogic currentStateLogic;

    private Observable<MATCH_STATE> stateSwitchObservableCurrent;
    private Observer<MATCH_STATE> stateSwitchObserver;

    private Observable<UUID> playerJoinObservableCurrent;
    private Observable<UUID> playerLeaveObservableCurrent;
    private Observer<UUID> playerJoinObserver;
    private Observer<UUID> playerLeaveObserver;

    private Map<UUID, Observable<ACK_TYPE>> playerAckObservableCurrent;
    private Map<UUID, Observer<ACK_TYPE>> playerAckObservers;

    private Map<UUID, Observable<PlayerData>> playerDataObservableCurrent;
    private Map<UUID, Observer<PlayerData>> playerDataObservers;

    private Map<UUID, Observable<MatchData>> matchDataObservableCurrent;
    private Map<UUID, Observer<MatchData>> matchDataObservers;

    // `RunningMatch` -> 외부 (`MatchRunnerService`)로 나가는 콜백. 예를 들어 데이터 가져오기, 데이터 수정 후 확정(?), state 변경 등
    @Setter
    private IMatchDataDelegate matchDataDelegate;

    public RunningMatch() {
        this.stateSwitchObservableCurrent = null;
        this.stateSwitchObserver = this::onMatchStateSwitch;

        this.playerJoinObservableCurrent = null;
        this.playerLeaveObservableCurrent = null;
        this.playerJoinObserver = this::onMatchPlayerJoin;
        this.playerLeaveObserver = this::onMatchPlayerLeave;

        this.playerDataObservableCurrent = new HashMap<>();
        this.playerDataObservers = new HashMap<>();

        this.playerAckObservableCurrent = new HashMap<>();
        this.playerAckObservers = new HashMap<>();

        this.matchDataObservableCurrent = new HashMap<>();
        this.matchDataObservers = new HashMap<>();

        this.currentState = null;
        this.currentStateLogic = null;

        this.matchId = null;
        this.timerHandle = null;

        this.matchDataDelegate = null;
    }

    /**
     * 해당 오브젝트를 주어진 ID에 대응하는 매치와 연결시킵니다.
     * Observer를 구독하는 등 설정을 해줍니다.
     *
     * @param matchId
     */
    public void connectMatch(UUID matchId) {
        // 이전 연결된 매치 연결 해제
        disconnectMatch();
        this.matchId = matchId;
    }

    /**
     * 해당 오브젝트에서 연결한 매치로부터 연결 해제합니다.
     * Observer를 구독해제하는 등 설정을 해줍니다.
     */
    public void disconnectMatch() {
        if (this.matchId != null) {
            timerHandle.cancel(false);

            playerAckObservers.forEach((playerId, _) -> {
                unsubscribeToPlayerAckEvents(playerId);
            });
            playerDataObservers.forEach((playerId, _) -> {
                unsubscribeToPlayerDataUpdates(playerId);
            });
            matchDataObservers.forEach((matchId, _) -> {
                unsubscribeToMatchDataUpdates(matchId);
            });

            unsubscribeToMatchPlayerJoinEvents();
            unsubscribeToMatchPlayerLeaveEvents();
            unsubscribeToMatchStateSwitchEvents();
        }

        this.matchId = null;
        this.timerHandle = null;
    }

    // State에서 불러지는 데이터 조회/설정 콜백 함수들
    // `RunningMatch` 내에서 리포지토리를 바로 DI 및 참조하기보단 외부에서 값을 받아서 넣어주는 방식으로 작동합니다. 안그럼 너무 많은 곳에서 리포지토리를 직접적으로 참조하는 문제가 발생하겠지요...
    public Optional<MatchData> getMatchData(UUID matchId) {
        return matchDataDelegate.getMatchData(matchId);
    }

    public Optional<PlayerData> getPlayerData(UUID playerId) {
        return matchDataDelegate.getPlayerData(playerId);
    }

    public void commitPlayerData(PlayerData playerData) {
        matchDataDelegate.commitPlayerData(playerData);
    }

    public void commitMatchData(MatchData matchData) {
        matchDataDelegate.commitMatchData(matchData);
    }

    public void commitFrdbData(MatchData matchData) {
        matchDataDelegate.commitFrdbData(matchData);
    }

    public boolean setTimerAndRun(int seconds, Runnable callback) {
        if (timerHandle != null && !timerHandle.isDone()) {
            System.err.printf("[MATCH] RunningMatch::setTimerAndRun(MATCH: %s) | TIMER ALREADY SET AND RUNNING!\n", matchId);
            return false;
        }

        timerHandle = matchDataDelegate.setTimerAndRun(matchId, seconds, callback);

        if (timerHandle == null) {
            System.err.printf("[MATCH] RunningMatch::setTimerAndRun(MATCH: %s) | TIMER SET FAILED!\n", matchId);
            return false;
        }

        return true;
    }

    public boolean cancelTimer() {
        if (timerHandle == null) {
            // System.err.printf("[MATCH] RunningMatch::cancelTimer(MATCH: %s) | TIMER NOT SET!\n", matchId);
            return false;
        }

        timerHandle.cancel(false);
        timerHandle = null;
        return true;
    }

    public void updateMatchDataForRoundBegin(MatchData matchData) {
        matchDataDelegate.updateMatchDataForRoundBegin(matchData);
    }

    public void updateMatchDataForTurnBegin(MatchData matchData) {
        matchDataDelegate.updateMatchDataForTurnBegin(matchData);
    }

    public void updateMatchDataForCalculateTurn(MatchData matchData) {
        matchDataDelegate.calculateTurn(matchData);
    }

    public void updateMatchDataForItemReceiving(MatchData matchData) {
        matchDataDelegate.receiveItemForAllPlayers(matchData);
    }

    public void updateMatchDataForSetPerkChoice(MatchData matchData) {
        matchDataDelegate.setPerkChoiceForAllPlayers(matchData);
    }

    // Observer 설정 함수들
    protected void subscribeToMatchDataUpdates(UUID matchId, FlowMappedObservable<MatchData, MatchData> observable) {
        matchDataObservers.putIfAbsent(matchId, matchData -> {
            onMatchDataUpdate(matchId, matchData);
        });
        Observer<MatchData> observer = matchDataObservers.getOrDefault(matchId, null);

        if (observer == null) {
            System.err.printf("[MATCH] RunningMatch::subscribeToMatchDataUpdates(MATCH: %s) | MATCH DATA UPDATES CREATE FAILED!\n", matchId);
            return;
        }

        observable.addObserver(observer);
        matchDataObservableCurrent.put(matchId, observable);

        // 기타 사이드이펙트들
        var matchStateObserver = new FlowMappedObservable<MatchData, MATCH_STATE>(MATCH_STATE.LOBBY_WAITING, true, true, true, value -> {
            if (value == null) {
                return null;
            }

            return value.getState();
        });
        var matchPlayerAckObserver = new FlowMappedObservable<MatchData, List<ACK_TYPE>>(List.of(), true, true, true, value -> {
            if (value == null) {
                return null;
            }

            return value.getPlayers().stream().map(PlayerData::getAckState).toList();
        });
        var matchPlayerSelectingObserver = new FlowMappedObservable<MatchData, List<Boolean>>(List.of(), true, true, true, value -> {
            if (value == null) {
                return null;
            }

            return value.getPlayers().stream().map(PlayerData::isSelecting).toList();
        });
        var matchPlayerListObserver = new FlowMappedObservable<MatchData, List<PlayerData>>(List.of(), true, true, true, value -> {
            if (value == null) {
                return null;
            }

            return value.getPlayers();
        });

        matchStateObserver.addObserver(this::onMatchStateSwitch);
        matchPlayerAckObserver.addObserver(this::onMatchPlayerAckStateUpdate);
        matchPlayerSelectingObserver.addObserver(this::onMatchPlayerSelectingStateUpdate);
        matchPlayerListObserver.addObserver(this::onMatchPlayerListUpdate);

        observable.addDownstreamObservable(matchStateObserver);
        observable.addDownstreamObservable(matchPlayerAckObserver);
        observable.addDownstreamObservable(matchPlayerListObserver);
        observable.addDownstreamObservable(matchPlayerSelectingObserver);
    }

    protected void unsubscribeToMatchDataUpdates(UUID matchId) {
        Observer<MatchData> observer = matchDataObservers.getOrDefault(matchId, null);
        Observable<MatchData> observable = matchDataObservableCurrent.getOrDefault(matchId, null);

        if (observer == null) {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchDataUpdates(MATCH: %s) | MATCH DATA UPDATES ARE NOT SUBSCRIBED YET! (observer = null)\n", matchId);
            return;
        }
        if (observable == null) {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchDataUpdates(MATCH: %s) | MATCH DATA UPDATES ARE NOT SUBSCRIBED YET?? (observable = null)\n", matchId);
            return;
        }

        observable.removeObserver(observer);
    }

    protected void subscribeToPlayerDataUpdates(UUID playerId, Observable<PlayerData> observable) {
        playerDataObservers.putIfAbsent(playerId, playerData -> {
            onPlayerDataUpdate(matchId, playerData);
        });
        Observer<PlayerData> observer = playerDataObservers.getOrDefault(playerId, null);

        if (observer == null) {
            System.err.printf("[PLR] RunningMatch::subscribeToPlayerDataUpdates(MATCH: %s, PLR: %s) | PLR DATA UPDATES CREATE FAILED!\n", matchId, playerId);
            return;
        }

        observable.addObserver(observer);
        playerDataObservableCurrent.put(playerId, observable);
    }

    protected void unsubscribeToPlayerDataUpdates(UUID playerId) {
        Observer<PlayerData> observer = playerDataObservers.getOrDefault(playerId, null);
        Observable<PlayerData> observable = playerDataObservableCurrent.getOrDefault(playerId, null);

        if (observer == null) {
            System.err.printf("[PLR] RunningMatch::unsubscribeToPlayerDataUpdates(MATCH: %s, PLR: %s) | PLR DATA UPDATES ARE NOT SUBSCRIBED YET! (observer = null)\n", matchId, playerId);
            return;
        }
        if (observable == null) {
            System.err.printf("[PLR] RunningMatch::unsubscribeToPlayerDataUpdates(MATCH: %s, PLR: %s) | PLR DATA UPDATES ARE NOT SUBSCRIBED YET?? (observable = null)\n", matchId, playerId);
            return;
        }

        observable.removeObserver(observer);
    }

    protected void subscribeToMatchStateSwitchEvents(Observable<MATCH_STATE> observable) {
        observable.addObserver(stateSwitchObserver);
        stateSwitchObservableCurrent = observable;
    }

    protected void unsubscribeToMatchStateSwitchEvents() {
        if (stateSwitchObservableCurrent != null) {
            stateSwitchObservableCurrent.removeObserver(stateSwitchObserver);
            stateSwitchObservableCurrent = null;
        } else {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchStateSwitchEvents(MATCH: %s) | STATE SWITCH EVENT IS NOT SUBSCRIBED YET!\n", matchId);
        }
    }

    protected void subscribeToMatchPlayerJoinEvents(Observable<UUID> observable) {
        observable.addObserver(playerJoinObserver);
        playerJoinObservableCurrent = observable;
    }

    protected void unsubscribeToMatchPlayerJoinEvents() {
        if (playerJoinObservableCurrent != null) {
            playerJoinObservableCurrent.removeObserver(playerJoinObserver);
            playerJoinObservableCurrent = null;
        } else {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchPlayerJoinEvents(MATCH: %s) | PLAYER JOIN EVENT IS NOT SUBSCRIBED YET!\n", matchId);
        }
    }

    protected void subscribeToMatchPlayerLeaveEvents(Observable<UUID> observable) {
        observable.addObserver(playerLeaveObserver);
        playerLeaveObservableCurrent = observable;
    }

    protected void unsubscribeToMatchPlayerLeaveEvents() {
        if (playerLeaveObservableCurrent != null) {
            playerLeaveObservableCurrent.removeObserver(playerLeaveObserver);
            playerLeaveObservableCurrent = null;
        } else {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchPlayerLeaveEvents(MATCH: %s) | PLAYER LEAVE EVENT IS NOT SUBSCRIBED YET!\n", matchId);
        }
    }

    protected void subscribeToPlayerAckEvents(UUID playerId, Observable<ACK_TYPE> observable) {
        playerAckObservers.putIfAbsent(playerId, type -> {
            onPlayerAck(playerId, type);
        });
        Observer<ACK_TYPE> observer = playerAckObservers.getOrDefault(playerId, null);

        if (observer == null) {
            System.err.printf("[PLR] RunningMatch::subscribeToPlayerAckEvents(MATCH: %s, PLR: %s) | PLAYER ACK EVENT CREATE FAILED!\n", matchId, playerId);
            return;
        }

        observable.addObserver(observer);
        playerAckObservableCurrent.put(playerId, observable);
    }

    protected void unsubscribeToPlayerAckEvents(UUID playerId) {
        Observer<ACK_TYPE> observer = playerAckObservers.getOrDefault(playerId, null);
        Observable<ACK_TYPE> observable = playerAckObservableCurrent.getOrDefault(playerId, null);

        if (observer == null) {
            System.err.printf("[PLR] RunningMatch::unsubscribeToPlayerAckEvents(MATCH: %s, PLR: %s) | PLAYER ACK EVENT IS NOT SUBSCRIBED YET! (observer = null)\n", matchId, playerId);
            return;
        }
        if (observable == null) {
            System.err.printf("[PLR] RunningMatch::unsubscribeToPlayerAckEvents(MATCH: %s, PLR: %s) | PLAYER ACK EVENT IS NOT SUBSCRIBED YET?? (observable = null)\n", matchId, playerId);
            return;
        }

        observable.removeObserver(observer);
    }

    /**
     * Observer 콜백: 플레이어 ack 이벤트
     *
     * @param playerId
     * @param type
     */
    private void onPlayerAck(UUID playerId, ACK_TYPE type) {
        System.out.printf("[PLR] RunningMatch::onPlayerAck(TYPE: %s, PLR: %s)\n", type, playerId);

        if (currentStateLogic == null) {
            System.err.printf("\t[MATCH] RunningMatch::onPlayerAck(PLR: %s) | STATE IS NULL!\n", playerId);
            return;
        }

        currentStateLogic.onPlayerAck(this, playerId, type);
    }

    /**
     * Observer 콜백: 플레이어가 매치에 입장
     *
     * @param playerId
     */
    private void onMatchPlayerJoin(UUID playerId) {
        System.out.printf("[MATCH] RunningMatch::onMatchPlayerJoin(MATCH: %s, PLR: %s)\n", matchId, playerId);

        if (currentStateLogic == null) {
            System.err.printf("\t[MATCH] RunningMatch::onMatchPlayerJoin(MATCH: %s) | STATE IS NULL!\n", matchId);
            return;
        }

        currentStateLogic.onPlayerJoin(this, playerId);
    }

    /**
     * Observer 콜백: 플레이어가 매치로부터 퇴장
     *
     * @param playerId
     */
    private void onMatchPlayerLeave(UUID playerId) {
        System.out.printf("[MATCH] RunningMatch::onMatchPlayerLeave(MATCH: %s, PLR: %s)\n", matchId, playerId);

        if (currentStateLogic == null) {
            System.err.printf("\t[MATCH] RunningMatch::onMatchPlayerLeave(MATCH: %s) | STATE IS NULL!\n", matchId);
            return;
        }

        currentStateLogic.onPlayerLeave(this, playerId);
    }

    /**
     * Observer 콜백: 매치 상태 변경
     *
     * @param newState
     */
    private void onMatchStateSwitch(MATCH_STATE newState) {
        if (this.currentState != newState) {
            System.out.printf("[MATCH] RunningMatch::onMatchStateSwitch(MATCH: %s, STATE: %s -> %s)\n", matchId, this.currentState, newState);

            if (newState == null) {
                System.err.printf("\t[MATCH] RunningMatch::onMatchStateSwitch(MATCH: %s) | NEW STATE IS NULL!\n", matchId);
                return;
            }

            if (currentStateLogic != null) {
                currentStateLogic.onExit(this);
            }

            currentState = newState;
            currentStateLogic = newState.getLogic();
            currentStateLogic.onEnter(this);
        }
    }

    private void onMatchPlayerListUpdate(List<PlayerData> players) {
        System.out.printf("[MATCH] RunningMatch::onMatchPlayerListUpdate(MATCH: %s) | %s\n", matchId, players.stream().map(player -> player.getId().toString()).collect(Collectors.joining(", ", "[", "]")));

        if (currentStateLogic == null) {
            System.err.printf("\t[MATCH] RunningMatch::onMatchPlayerListUpdate(MATCH: %s) | STATE IS NULL!\n", matchId);
            return;
        }

        currentStateLogic.onMatchPlayerListUpdate(this, players);
    }

    private void onMatchPlayerAckStateUpdate(List<ACK_TYPE> ackState) {
        System.out.printf("[MATCH] RunningMatch::onMatchPlayerAckStateUpdate(MATCH: %s) | %s\n", matchId, ackState);

        if (currentStateLogic == null) {
            System.err.printf("\t[MATCH] RunningMatch::onMatchPlayerAckStateUpdate(MATCH: %s) | STATE IS NULL!\n", matchId);
            return;
        }

        currentStateLogic.onMatchPlayerAckStateUpdate(this, ackState);
    }

    private void onMatchPlayerSelectingStateUpdate(List<Boolean> selectingState) {
        System.out.printf("[MATCH] RunningMatch::onMatchPlayerSelectingStateUpdate(MATCH: %s) | %s\n", matchId, selectingState);

        if (currentStateLogic == null) {
            System.err.printf("\t[MATCH] RunningMatch::onMatchPlayerSelectingStateUpdate(MATCH: %s) | STATE IS NULL!\n", matchId);
            return;
        }

        currentStateLogic.onMatchPlayerSelectingStateUpdate(this, selectingState);
    }

    private void onMatchDataUpdate(UUID matchId, MatchData matchData) {
        // if (matchData == null) {
        //     System.out.printf("[MATCH] RunningMatch::onMatchDataUpdate(MATCH: %s) | <NULL>\n", matchId);
        // } else {
        //     System.out.printf("[MATCH] RunningMatch::onMatchDataUpdate(MATCH: %s) | %s, %s\n", matchId, matchData.getState(), FrdbConstants.TIME_FORMATTER.format(matchData.getLastUpdated()));
        // }
    }

    private void onPlayerDataUpdate(UUID playerId, PlayerData playerData) {
        // System.out.printf("[PLR] RunningMatch::onPlayerDataUpdate(MATCH: %s, PLR: %s) | %s\n", matchId, playerId, playerData);
    }
}
