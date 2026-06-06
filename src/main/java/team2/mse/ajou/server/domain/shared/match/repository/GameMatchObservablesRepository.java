package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.observer.DefaultObservable;
import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class GameMatchObservablesRepository implements GameObservablesRepository {
    private final Map<UUID, Observable<UUID>> matchPlayerJoinEventObservables;
    private final Map<UUID, Observable<UUID>> matchPlayerLeaveEventObservables;
    private final Map<UUID, Observable<MATCH_STATE>> matchStateSwitchEventObservables;

    private final Map<UUID, Observable<ACK_TYPE>> playerAckEventObservables;

    private final Map<UUID, Observable<MatchData>> matchDataObservables;

    public GameMatchObservablesRepository() {
        this.matchPlayerJoinEventObservables = new HashMap<>();
        this.matchPlayerLeaveEventObservables = new HashMap<>();
        this.matchStateSwitchEventObservables = new HashMap<>();

        this.playerAckEventObservables = new HashMap<>();

        this.matchDataObservables = new HashMap<>();
    }

    @Override
    public Observable<MatchData> getMatchDataObservable(UUID matchId) {
        return fetchOrCreateMatchDataObservable(matchId);
    }

    @Override
    public Observable<UUID> getMatchPlayerJoinEventsObservable(UUID matchId) {
        return fetchOrCreateMatchPlayerJoinEventObservable(matchId);
    }

    @Override
    public Observable<UUID> getMatchPlayerLeaveEventsObservable(UUID matchId) {
        return fetchOrCreateMatchPlayerLeaveEventObservable(matchId);
    }

    @Override
    public Observable<MATCH_STATE> getMatchStateSwitchEventsObservable(UUID matchId) {
        return fetchOrCreateMatchStateSwitchEventObservable(matchId);
    }

    @Override
    public void sendMatchPlayerJoinEvent(UUID matchId, UUID playerId) {
        var observable = fetchOrCreateMatchPlayerJoinEventObservable(matchId);
        observable.updateValue(playerId);
    }

    @Override
    public void sendMatchPlayerLeaveEvent(UUID matchId, UUID playerId) {
        var observable = fetchOrCreateMatchPlayerLeaveEventObservable(matchId);
        observable.updateValue(playerId);
    }

    @Override
    public void sendMatchStateSwitch(UUID matchId, MATCH_STATE newState) {
        var observable = fetchOrCreateMatchStateSwitchEventObservable(matchId);
        observable.updateValue(newState);
    }

    @Override
    public Observable<ACK_TYPE> getPlayerAckEventsObservable(UUID playerId) {
        return fetchOrCreatePlayerAckEventObservable(playerId);
    }

    @Override
    public void sendPlayerAckEvent(UUID playerId, ACK_TYPE type) {
        var observable = fetchOrCreatePlayerAckEventObservable(playerId);
        observable.updateValue(type);
    }

    private Observable<MatchData> fetchOrCreateMatchDataObservable(UUID matchId) {
        return fetchOrCreateObservable(matchDataObservables, matchId, null, true, false);
    }

    private Observable<UUID> fetchOrCreateMatchPlayerJoinEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerJoinEventObservables, matchId, null, false, false);
    }

    private Observable<UUID> fetchOrCreateMatchPlayerLeaveEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerLeaveEventObservables, matchId, null, false, false);
    }

    private Observable<MATCH_STATE> fetchOrCreateMatchStateSwitchEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchStateSwitchEventObservables, matchId, MATCH_STATE.LOBBY_WAITING, true, true);
    }

    private Observable<ACK_TYPE> fetchOrCreatePlayerAckEventObservable(UUID playerId) {
        return fetchOrCreateObservable(playerAckEventObservables, playerId, ACK_TYPE.NO_ACK, true, true);
    }

    /**
     * Map에 주어진 ID값에 대응하는 Observable가 있으면 그것을 반환하고, 없으면 새로 생성해서 반환합니다.
     *
     * @param map
     * @param id
     * @param initialValue
     * @param isIgnoreDuplicateValue
     * @param isNotifyOnSubscribe
     * @param <T>
     * @return
     */
    private <T> Observable<T> fetchOrCreateObservable(
            Map<UUID, Observable<T>> map,
            UUID id,
            T initialValue,
            boolean isIgnoreDuplicateValue,
            boolean isNotifyOnSubscribe
    ) {
        map.putIfAbsent(id, new DefaultObservable<>(initialValue, isIgnoreDuplicateValue, isNotifyOnSubscribe));
        return map.get(id);
    }
}
