package team2.mse.ajou.server.domain.shared.match.service;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.states.MatchStateLogic;
import team2.mse.ajou.server.domain.shared.observer.Observable;
import team2.mse.ajou.server.domain.shared.observer.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

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
    protected UUID matchId;
    protected ScheduledFuture<?> timerHandle;

    protected MATCH_STATE currentState;
    protected MatchStateLogic currentStateLogic;

    protected Observable<MATCH_STATE> stateSwitchObservableCurrent;
    protected Observer<MATCH_STATE> stateSwitchObserver;

    protected Observable<UUID> playerJoinObservableCurrent;
    protected Observable<UUID> playerLeaveObservableCurrent;
    protected Observer<UUID> playerJoinObserver;
    protected Observer<UUID> playerLeaveObserver;

    protected Map<UUID, Observable<ACK_TYPE>> playerAckObservableCurrent;
    protected Map<UUID, Observer<ACK_TYPE>> playerAckObservers;

    public RunningMatch() {
        this.stateSwitchObservableCurrent = null;
        this.stateSwitchObserver = this::onMatchStateSwitch;

        this.playerJoinObservableCurrent = null;
        this.playerLeaveObservableCurrent = null;
        this.playerJoinObserver = this::onMatchPlayerJoin;
        this.playerLeaveObserver = this::onMatchPlayerLeave;

        this.playerAckObservableCurrent = new HashMap<>();
        this.playerAckObservers = new HashMap<>();

        this.currentState = null;
        this.currentStateLogic = null;

        this.matchId = null;
        this.timerHandle = null;
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

            playerAckObservers.forEach((playerId, observer) -> {
                unsubscribeToPlayerAckEvents(playerId);
            });

            unsubscribeToMatchPlayerJoinEvents();
            unsubscribeToMatchPlayerLeaveEvents();
            unsubscribeToMatchStateSwitchEvents();
        }

        this.matchId = null;
        this.timerHandle = null;
    }

    public void subscribeToMatchStateSwitchEvents(Observable<MATCH_STATE> observable) {
        observable.addObserver(stateSwitchObserver);
        stateSwitchObservableCurrent = observable;
    }

    public void unsubscribeToMatchStateSwitchEvents() {
        if (stateSwitchObservableCurrent != null) {
            stateSwitchObservableCurrent.removeObserver(stateSwitchObserver);
            stateSwitchObservableCurrent = null;
        } else {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchStateSwitchEvents(MATCH: %s) | STATE SWITCH EVENT IS NOT SUBSCRIBED YET!\n", matchId);
        }
    }

    public void subscribeToMatchPlayerJoinEvents(Observable<UUID> observable) {
        observable.addObserver(playerJoinObserver);
        playerJoinObservableCurrent = observable;
    }

    public void unsubscribeToMatchPlayerJoinEvents() {
        if (playerJoinObservableCurrent != null) {
            playerJoinObservableCurrent.removeObserver(playerJoinObserver);
            playerJoinObservableCurrent = null;
        } else {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchPlayerJoinEvents(MATCH: %s) | PLAYER JOIN EVENT IS NOT SUBSCRIBED YET!\n", matchId);
        }
    }

    public void subscribeToMatchPlayerLeaveEvents(Observable<UUID> observable) {
        observable.addObserver(playerLeaveObserver);
        playerLeaveObservableCurrent = observable;
    }

    public void unsubscribeToMatchPlayerLeaveEvents() {
        if (playerLeaveObservableCurrent != null) {
            playerLeaveObservableCurrent.removeObserver(playerLeaveObserver);
            playerLeaveObservableCurrent = null;
        } else {
            System.err.printf("[MATCH] RunningMatch::unsubscribeToMatchPlayerLeaveEvents(MATCH: %s) | PLAYER LEAVE EVENT IS NOT SUBSCRIBED YET!\n", matchId);
        }
    }

    public void subscribeToPlayerAckEvents(UUID playerId, Observable<ACK_TYPE> observable) {
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

    public void unsubscribeToPlayerAckEvents(UUID playerId) {
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

        currentStateLogic.onPlayerAck(playerId, type);
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

        currentStateLogic.onPlayerJoin(playerId);
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

        currentStateLogic.onPlayerLeave(playerId);
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
                currentStateLogic.onExit();
            }

            currentState = newState;
            currentStateLogic = newState.getLogic();
            currentStateLogic.onEnter();
        }
    }
}
