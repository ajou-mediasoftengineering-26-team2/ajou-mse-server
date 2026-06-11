package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGameDataRepository {
    /**
     * Match: find by ID.
     */
    Optional<MatchData> findMatchById(UUID matchId);

    /**
     * Match: find by player ID, by searching for players first, then checking their joined match.
     */
    Optional<MatchData> findMatchByJoinedPlayerId(UUID playerId);

    /**
     * Match: get list of all matches.
     */
    List<MatchData> findAllMatches();

    /**
     * Player: find by ID.
     */
    Optional<PlayerData> findPlayerById(UUID playerId);

    /**
     * Player: check any player exists with given username.
     */
    boolean isPlayerExistsByUsername(String name);

    /**
     * Player: check any player exists with given ID.
     */
    boolean isPlayerExistsById(UUID playerId);

    /**
     * Match: save to DB.
     */
    MatchData saveMatch(MatchData data);

    /**
     * Player: save to DB.
     */
    PlayerData savePlayer(PlayerData data);

    /**
     * Match: delete by ID.
     */
    void deleteMatchById(UUID matchId);

    /**
     * Player: delete by ID.
     */
    void deletePlayerById(UUID playerId);

    /**
     * Match: Update FRDB.
     */
    void updateFrdbMatchData(MatchData data);
}

