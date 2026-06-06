package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.Optional;
import java.util.UUID;

public interface GameDataRepository extends MatchEventsRepository, PlayerEventsRepository {
    Optional<MatchData> findMatchById(UUID matchId);

    Optional<PlayerData> findPlayerById(UUID playerId);

    MatchData saveMatchData(MatchData data);

    PlayerData savePlayerData(PlayerData data);

    void updateFrdbMatchData(MatchData data);
}
