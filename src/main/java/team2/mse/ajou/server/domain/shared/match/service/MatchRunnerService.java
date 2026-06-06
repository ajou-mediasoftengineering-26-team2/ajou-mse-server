package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.MatchEventsRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerEventsRepository;
import team2.mse.ajou.server.domain.shared.match.states.MatchStateLogic;
import team2.mse.ajou.server.domain.shared.observer.Observable;
import team2.mse.ajou.server.domain.shared.observer.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * 상태별 매치의 로직을 실행하는 서비스.
 * Service for running logics for match.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class MatchRunnerService {
    // 현재 관리중인 (i.e. 옵저버가 돌아가는) 매치들
    Map<UUID, RunningMatch> allRunningMatches;

    public MatchRunnerService(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    public void addMatch(UUID matchId) {
        var data = new RunningMatch();

        data.connectMatch(matchId);
        data.subscribeToMatchPlayerJoinEvents(gameDataRepository.getPlayerJoinEventsObservable(matchId));
        data.subscribeToMatchPlayerLeaveEvents(gameDataRepository.getPlayerLeaveEventsObservable(matchId));
        //gameDataRepository.subscribeToPlayerAckEvent(matchId, data.playerAckObservers);

        allRunningMatches.put(matchId, data);
    }

    public void removeMatch(UUID matchId) {
        var data = allRunningMatches.getOrDefault(matchId, null);

        if (data != null) {
            System.out.printf("[%s] MATCH: DISCONNECTING PREVIOUSLY CONNECTED MATCH!\n", matchId);
            data.disconnectMatch();
        }

        allRunningMatches.remove(matchId);
    }

    // 리포지토리들
    private final GameDataRepository gameDataRepository;

    /**
     * 매치별 내부 데이터 (게임과 연관없는 데이터) 관리용 클래스.
     * 아래 데이터를 관리합니다.
     * - 현재 State와 대응하는 State 구현
     * - 매치에서 리스닝 중인 `Observable`들 (플레이어)
     * - 타이머 기능이 필요할 때를 대비한 `ScheduledFuture`
     *
     * @author Ahn Yubin / 202021088
     */
    public static class RunningMatch {
        protected UUID matchId;
        protected ScheduledFuture<?> timerHandle;

        protected MatchStateLogic currentState;

        protected Observable<UUID> playerJoinObservableCurrent;
        protected Observable<UUID> playerLeaveObservableCurrent;
        protected Observer<UUID> playerJoinObserver;
        protected Observer<UUID> playerLeaveObserver;

        protected Map<UUID, Observable<ACK_TYPE>> playerAckObservableCurrent;
        protected Map<UUID, Observer<ACK_TYPE>> playerAckObservers;

        public RunningMatch() {
            this.playerJoinObserver = this::onMatchPlayerJoin;
            this.playerLeaveObserver = this::onMatchPlayerLeave;

            this.playerAckObservers = new HashMap<>();

            this.currentState = null;
            this.matchId = null;
            this.timerHandle = null;
        }

        /**
         * 해당 오브젝트를 주어진 ID에 대응하는 매치와 연결시킵니다.
         * Observer를 구독하는 등 설정을 해줍니다.
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
                playerAckObservers.forEach((playerId, observer) -> {
                    unsubscribeToPlayerAckEvents(playerId);
                });

                unsubscribeToMatchPlayerJoinEvents();
                unsubscribeToMatchPlayerLeaveEvents();
            }

            this.matchId = null;
        }

        /**
         * Observer 콜백: 플레이어 ack 이벤트
         * @param playerId
         * @param type
         */
        private void onPlayerAck(UUID playerId, ACK_TYPE type) {
            System.out.printf("[%s] ON PLAYER ACK (%s) - PLR ID: %s\n", matchId, type, playerId);
        }

        /**
         * Observer 콜백: 플레이어가 매치에 입장
         * @param playerId
         */
        private void onMatchPlayerJoin(UUID playerId) {
            System.out.printf("[%s] ON PLAYER JOIN - PLR ID: %s\n", matchId, playerId);

            if (currentState == null) {
                System.err.printf("[%s] PLAYER JOIN: STATE IS NULL!\n", matchId);
                return;
            }

            currentState.onPlayerJoin(playerId);
        }

        /**
         * Observer 콜백: 플레이어가 매치로부터 퇴장
         * @param playerId
         */
        private void onMatchPlayerLeave(UUID playerId) {
            System.out.printf("[%s] ON PLAYER LEAVE - PLR ID: %s\n", matchId, playerId);

            if (currentState == null) {
                System.err.printf("[%s] PLAYER JOIN: STATE IS NULL!\n", matchId);
                return;
            }

            currentState.onPlayerLeave(playerId);
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
                System.err.printf("[%s] MATCH PLAYER JOIN EVENT IS NOT SUBSCRIBED YET!\n", matchId);
            }
        }

        private void subscribeToMatchPlayerLeaveEvents(Observable<UUID> observable) {
            observable.addObserver(playerLeaveObserver);
            playerLeaveObservableCurrent = observable;
        }

        private void unsubscribeToMatchPlayerLeaveEvents() {
            if (playerLeaveObservableCurrent != null) {
                playerLeaveObservableCurrent.removeObserver(playerLeaveObserver);
                playerLeaveObservableCurrent = null;
            } else {
                System.err.printf("[%s] MATCH PLAYER LEAVE EVENT IS NOT SUBSCRIBED YET!\n", matchId);
            }
        }

        private void subscribeToPlayerAckEvents(UUID playerId, Observable<ACK_TYPE> observable) {
            playerAckObservers.putIfAbsent(playerId, type -> {
                onPlayerAck(playerId, type);
            });
            Observer<ACK_TYPE> observer = playerAckObservers.getOrDefault(playerId, null);

            if (observer == null) {
                System.err.printf("[%s] PLAYER ACK EVENT IS NOT READY TO SUBSCRIBE! - PLR ID: %s\n", matchId, playerId);
                return;
            }

            observable.addObserver(observer);
            playerAckObservableCurrent.put(playerId, observable);
        }

        private void unsubscribeToPlayerAckEvents(UUID playerId) {
            Observer<ACK_TYPE> observer = playerAckObservers.getOrDefault(playerId, null);
            Observable<ACK_TYPE> observable = playerAckObservableCurrent.getOrDefault(playerId, null);

            if (observer == null) {
                System.err.printf("[%s] PLAYER ACK EVENT IS NOT READY TO UNSUBSCRIBE! - PLR ID: %s\n", matchId, playerId);
                return;
            }
            if (observable == null) {
                System.err.printf("[%s] PLAYER ACK EVENT IS NOT ASSIGNED (SUBSCRIBED YET?)! - PLR ID: %s\n", matchId, playerId);
                return;
            }

            observable.removeObserver(observer);
        }
    }
}