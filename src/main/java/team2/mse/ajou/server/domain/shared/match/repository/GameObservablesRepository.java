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

/**
 * Implementation of combined repository of `IMatchObservablesRepository` and `IPlayerObservablesRepository`.
 */
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

    /**
     * Gets observable `MatchData` that is updated whenever it's written to DB.
     *
     * @param matchId ID.
     * @return Observable.
     */
    @Override
    public FlowMappedObservable<MatchData, MatchData> getMatchDataObservable(UUID matchId) {
        return fetchOrCreateMatchDataObservable(matchId);
    }

    /**
     * Gets observable updates whenever any player joins a match.
     *
     * @param matchId ID.
     * @return Observable, joined player ID.
     */
    @Override
    public Observable<UUID> getMatchPlayerJoinEventsObservable(UUID matchId) {
        return fetchOrCreateMatchPlayerJoinEventObservable(matchId);
    }

    /**
     * Gets observable updates whenever any player leaves a match.
     *
     * @param matchId ID.
     * @return Observable, left player ID.
     */
    @Override
    public Observable<UUID> getMatchPlayerLeaveEventsObservable(UUID matchId) {
        return fetchOrCreateMatchPlayerLeaveEventObservable(matchId);
    }

    /**
     * Gets observable updates whenever a match switches state.
     *
     * @param matchId ID.
     * @return Observable, new state.
     */
    @Override
    public Observable<MATCH_STATE> getMatchStateSwitchEventsObservable(UUID matchId) {
        return fetchOrCreateMatchStateSwitchEventObservable(matchId);
    }

    /**
     * Sends new `MatchData` notice.
     *
     * @param matchData New `MatchData`.
     */
    @Override
    public void sendMatchDataUpdate(MatchData matchData) {
        var observable = fetchOrCreateMatchDataObservable(matchData.getId());
        observable.updateValue(matchData);
    }

    /**
     * Sends new player join notice.
     *
     * @param matchId  Match ID.
     * @param playerId Player ID.
     */
    @Override
    public void sendMatchPlayerJoinEvent(UUID matchId, UUID playerId) {
        var observable = fetchOrCreateMatchPlayerJoinEventObservable(matchId);
        observable.updateValue(playerId);
    }

    /**
     * Sends player left notice.
     *
     * @param matchId  Match ID.
     * @param playerId Player ID.
     */
    @Override
    public void sendMatchPlayerLeaveEvent(UUID matchId, UUID playerId) {
        var observable = fetchOrCreateMatchPlayerLeaveEventObservable(matchId);
        observable.updateValue(playerId);
    }

    /**
     * Sends match state switch notice.
     *
     * @param matchId  Match ID.
     * @param newState Switched state.
     */
    @Override
    public void sendMatchStateSwitch(UUID matchId, MATCH_STATE newState) {
        var observable = fetchOrCreateMatchStateSwitchEventObservable(matchId);
        observable.updateValue(newState);
    }

    /**
     * Gets observable `PlayerData` that is updated whenever it's written to DB.
     *
     * @param playerId ID.
     * @return Observable.
     */
    @Override
    public Observable<PlayerData> getPlayerDataObservable(UUID playerId) {
        return fetchOrCreatePlayerDataObservable(playerId);
    }

    /**
     * Gets observable `ACK_TYPE` for given player.
     *
     * @param playerId ID.
     * @return Observable, sent `ACK_TYPE`.
     */
    @Override
    public Observable<ACK_TYPE> getPlayerAckEventsObservable(UUID playerId) {
        return fetchOrCreatePlayerAckEventObservable(playerId);
    }

    /**
     * Sends new `PlayerData` notice.
     *
     * @param playerData New `PlayerData`.
     */
    @Override
    public void sendPlayerDataUpdate(PlayerData playerData) {
        var observable = fetchOrCreatePlayerDataObservable(playerData.getId());
        observable.updateValue(playerData);
    }

    /**
     * Sends new `ACK_TYPE` notice.
     *
     * @param playerId Player ID.
     * @param type     ACK.
     */
    @Override
    public void sendPlayerAckEvent(UUID playerId, ACK_TYPE type) {
        var observable = fetchOrCreatePlayerAckEventObservable(playerId);
        observable.updateValue(type);
    }

    /**
     * Helper: create observable for `MatchData`.
     */
    private FlowMappedObservable<MatchData, MatchData> fetchOrCreateMatchDataObservable(UUID matchId) {
        return fetchOrCreateObservable(matchDataObservables, matchId, null, true, true, true);
    }

    /**
     * Helper: create observable for `PlayerData`.
     */
    private FlowMappedObservable<PlayerData, PlayerData> fetchOrCreatePlayerDataObservable(UUID playerId) {
        return fetchOrCreateObservable(playerDataObservables, playerId, null, true, true, true);
    }

    /**
     * Helper: create observable for player join event.
     */
    private FlowMappedObservable<UUID, UUID> fetchOrCreateMatchPlayerJoinEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerJoinEventObservables, matchId, null, true, false, false);
    }

    /**
     * Helper: create observable for player left event.
     */
    private FlowMappedObservable<UUID, UUID> fetchOrCreateMatchPlayerLeaveEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerLeaveEventObservables, matchId, null, true, false, false);
    }

    /**
     * Helper: create observable for match state switch event.
     */
    private FlowMappedObservable<MATCH_STATE, MATCH_STATE> fetchOrCreateMatchStateSwitchEventObservable(UUID matchId) {
        return fetchOrCreateObservable(matchStateSwitchEventObservables, matchId, MATCH_STATE.LOBBY_WAITING, true, true, true);
    }

    /**
     * Helper: create observable for player ACK event.
     */
    private FlowMappedObservable<ACK_TYPE, ACK_TYPE> fetchOrCreatePlayerAckEventObservable(UUID playerId) {
        return fetchOrCreateObservable(playerAckEventObservables, playerId, ACK_TYPE.NO_ACK, true, true, true);
    }

    /**
     * Helper: If there are and `Observable` with given ID as a key in the Map, return it. Otherwise, make a new `Observable` and return it.
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
