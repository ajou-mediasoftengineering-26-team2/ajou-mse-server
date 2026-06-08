package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.observer.FlowMappedObservable;
import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class GameObservablesRepository implements IGameObservablesRepository {
    private final Map<UUID, FlowMappedObservable<UUID, UUID>> matchPlayerJoinEventObservables;
    private final Map<UUID, FlowMappedObservable<UUID, UUID>> matchPlayerLeaveEventObservables;
    private final Map<UUID, FlowMappedObservable<MATCH_STATE, MATCH_STATE>> matchStateSwitchEventObservables;

    private final Map<UUID, FlowMappedObservable<ACK_TYPE, ACK_TYPE>> playerAckEventObservables;

    private final Map<UUID, FlowMappedObservable<PlayerData, PlayerData>> playerDataObservables;
    private final Map<UUID, FlowMappedObservable<MatchData, MatchData>> matchDataObservables;

    public GameObservablesRepository() {
        this.matchPlayerJoinEventObservables = new HashMap<>();
        this.matchPlayerLeaveEventObservables = new HashMap<>();
        this.matchStateSwitchEventObservables = new HashMap<>();

        this.playerAckEventObservables = new HashMap<>();

        this.playerDataObservables = new HashMap<>();
        this.matchDataObservables = new HashMap<>();
    }

    @Override
    public FlowMappedObservable<MatchData, MatchData> getMatchDataObservable(UUID matchId) {
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
    public void sendMatchDataUpdate(MatchData matchData) {
        var observable = fetchOrCreateMatchDataObservable(matchData.getId());
        observable.updateValue(matchData);
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
    public Observable<PlayerData> getPlayerDataObservable(UUID playerId) {
        return fetchOrCreatePlayerDataObservable(playerId);
    }

    @Override
    public Observable<ACK_TYPE> getPlayerAckEventsObservable(UUID playerId) {
        return fetchOrCreatePlayerAckEventObservable(playerId);
    }

    @Override
    public void sendPlayerDataUpdate(PlayerData playerData) {
        var observable = fetchOrCreatePlayerDataObservable(playerData.getId());
        observable.updateValue(playerData);
    }

    @Override
    public void sendPlayerAckEvent(UUID playerId, ACK_TYPE type) {
        var observable = fetchOrCreatePlayerAckEventObservable(playerId);
        observable.updateValue(type);
    }

    private FlowMappedObservable<MatchData, MatchData> fetchOrCreateMatchDataObservable(UUID matchId) {
        return fetchOrCreateObservable(matchDataObservables, matchId, null, true, true, true);
    }

    private FlowMappedObservable<PlayerData, PlayerData> fetchOrCreatePlayerDataObservable(UUID playerId) {
        return fetchOrCreateObservable(playerDataObservables, playerId, null, true, true, true);
    }

    private FlowMappedObservable<UUID, UUID> fetchOrCreateMatchPlayerJoinEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerJoinEventObservables, matchId, null, true, false, false);
    }

    private FlowMappedObservable<UUID, UUID> fetchOrCreateMatchPlayerLeaveEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerLeaveEventObservables, matchId, null, true, false, false);
    }

    private FlowMappedObservable<MATCH_STATE, MATCH_STATE> fetchOrCreateMatchStateSwitchEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchStateSwitchEventObservables, matchId, MATCH_STATE.LOBBY_WAITING, true, true, true);
    }

    private FlowMappedObservable<ACK_TYPE, ACK_TYPE> fetchOrCreatePlayerAckEventObservable(UUID playerId) {
        return fetchOrCreateObservable(playerAckEventObservables, playerId, ACK_TYPE.NO_ACK, true, true, true);
    }

    /**
     * Map에 주어진 ID값에 대응하는 Observable가 있으면 그것을 반환하고, 없으면 새로 생성해서 반환합니다.
     *
     * @param map
     * @param id
     * @param initialValue
     * @param isIgnoreNull
     * @param isIgnoreDuplicateValue
     * @param isNotifyOnSubscribe
     * @param <T>
     * @return
     */
    private <T> FlowMappedObservable<T, T> fetchOrCreateObservable(
            Map<UUID, FlowMappedObservable<T, T>> map,
            UUID id,
            T initialValue,
            boolean isIgnoreNull,
            boolean isIgnoreDuplicateValue,
            boolean isNotifyOnSubscribe
    ) {
        map.putIfAbsent(id, new FlowMappedObservable<T, T>(initialValue, isIgnoreNull, isIgnoreDuplicateValue, isNotifyOnSubscribe, value -> value));
        return map.get(id);
    }
}
