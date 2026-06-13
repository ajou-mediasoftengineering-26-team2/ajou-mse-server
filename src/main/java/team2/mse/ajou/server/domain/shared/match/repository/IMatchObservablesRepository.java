package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.observer.FlowMappedObservable;
import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.UUID;

/**
 * Repository that provides match related observable data.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IMatchObservablesRepository {
    /**
     * Gets observable `MatchData` that is updated whenever it's written to DB.
     *
     * @param matchId ID.
     * @return Observable.
     */
    FlowMappedObservable<MatchData, MatchData> getMatchDataObservable(UUID matchId);

    /**
     * Gets observable updates whenever any player joins a match.
     *
     * @param matchId ID.
     * @return Observable, joined player ID.
     */
    Observable<UUID> getMatchPlayerJoinEventsObservable(UUID matchId);

    /**
     * Gets observable updates whenever any player leaves a match.
     *
     * @param matchId ID.
     * @return Observable, left player ID.
     */
    Observable<UUID> getMatchPlayerLeaveEventsObservable(UUID matchId);

    /**
     * Gets observable updates whenever a match switches state.
     *
     * @param matchId ID.
     * @return Observable, new state.
     */
    Observable<MATCH_STATE> getMatchStateSwitchEventsObservable(UUID matchId);

    /**
     * Sends new `MatchData` notice.
     *
     * @param matchData New `MatchData`.
     */
    void sendMatchDataUpdate(MatchData matchData);

    /**
     * Sends new player join notice.
     *
     * @param matchId  Match ID.
     * @param playerId Player ID.
     */
    void sendMatchPlayerJoinEvent(UUID matchId, UUID playerId);

    /**
     * Sends player left notice.
     *
     * @param matchId  Match ID.
     * @param playerId Player ID.
     */
    void sendMatchPlayerLeaveEvent(UUID matchId, UUID playerId);

    /**
     * Sends match state switch notice.
     *
     * @param matchId  Match ID.
     * @param newState Switched state.
     */
    void sendMatchStateSwitch(UUID matchId, MATCH_STATE newState);
}
