package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.UUID;

public interface MatchObservablesRepository {
    Observable<MatchData> getMatchDataObservable(UUID matchId);

    Observable<UUID> getMatchPlayerJoinEventsObservable(UUID matchId);

    Observable<UUID> getMatchPlayerLeaveEventsObservable(UUID matchId);

    Observable<MATCH_STATE> getMatchStateSwitchEventsObservable(UUID matchId);

    void sendMatchDataUpdate(MatchData matchData);

    void sendMatchPlayerJoinEvent(UUID matchId, UUID playerId);

    void sendMatchPlayerLeaveEvent(UUID matchId, UUID playerId);

    void sendMatchStateSwitch(UUID matchId, MATCH_STATE newState);
}
