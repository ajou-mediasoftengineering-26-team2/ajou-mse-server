package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.UUID;

public interface MatchEventsRepository {
    Observable<UUID> getPlayerJoinEventsObservable(UUID matchId);

    Observable<UUID> getPlayerLeaveEventsObservable(UUID matchId);
}
