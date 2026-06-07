package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameDataRepository {
    Optional<MatchData> findMatchById(UUID matchId);

    Optional<MatchData> findMatchByJoinedPlayerId(UUID playerId);

    List<MatchData> findAllMatches();

    Optional<PlayerData> findPlayerById(UUID playerId);

    boolean isPlayerExistsByUsername(String name);

    boolean isPlayerExistsById(UUID playerId);

    MatchData saveMatch(MatchData data);

    PlayerData savePlayer(PlayerData data);

    void deleteMatchById(UUID matchId);

    void deletePlayerById(UUID playerId);

    void updateFrdbMatchData(MatchData data);
}

