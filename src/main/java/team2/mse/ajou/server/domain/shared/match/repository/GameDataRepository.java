package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.firebase.service.IFrdbRepository;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 'Composite' Repository that aggregates all the repository, including Firebase DB and JPA repositories.
 * This is because `PlayerData` and `MatchData` depends on each other.
 * So updating with separate repositories makes it hard to do inter-DB(?) operations, such as sending update events for associated `MatchData` whenever `PlayerData` is modified.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
public class GameDataRepository implements IGameDataRepository {
    // 리포지토리들
    private final MatchDataJpaRepository matchDataJpaRepository;
    private final PlayerDataJpaRepository playerDataJpaRepository;
    private final IFrdbRepository frdbRepository;

    public GameDataRepository(
            MatchDataJpaRepository matchDataJpaRepository,
            PlayerDataJpaRepository playerDataJpaRepository,
            IFrdbRepository frdbRepository
    ) {
        this.matchDataJpaRepository = matchDataJpaRepository;
        this.playerDataJpaRepository = playerDataJpaRepository;
        this.frdbRepository = frdbRepository;
    }

    /**
     * Match: find by ID.
     */
    @Override
    public Optional<MatchData> findMatchById(UUID matchId) {
        return matchDataJpaRepository.findById(matchId);
    }

    /**
     * Match: find by player ID, by searching for players first, then checking their joined match.
     */
    @Override
    public Optional<MatchData> findMatchByJoinedPlayerId(UUID playerId) {
        PlayerData playerData = playerDataJpaRepository.findById(playerId).orElse(null);
        if (playerData == null) {
            return Optional.empty();
        }

        return matchDataJpaRepository.findById(playerData.getJoinedMatchId());
    }

    /**
     * Match: get list of all matches.
     */
    @Override
    public List<MatchData> findAllMatches() {
        return matchDataJpaRepository.findAll();
    }

    /**
     * Player: find by ID.
     */
    @Override
    public Optional<PlayerData> findPlayerById(UUID playerId) {
        return playerDataJpaRepository.findById(playerId);
    }

    /**
     * Player: check any player exists with given username.
     */
    @Override
    public boolean isPlayerExistsByUsername(String name) {
        return playerDataJpaRepository.existsByUsername(name);
    }

    /**
     * Player: check any player exists with given ID.
     */
    @Override
    public boolean isPlayerExistsById(UUID playerId) {
        return playerDataJpaRepository.existsById(playerId);
    }

    /**
     * Match: save to DB.
     */
    @Override
    public MatchData saveMatch(MatchData data) {
        data.updateLastUpdated();

        MatchData matchData = matchDataJpaRepository.save(data);

        // 플레이어 DB도 갱신
        playerDataJpaRepository.saveAll(matchData.getPlayers());

        return matchDataJpaRepository.save(data);
    }

    /**
     * Player: save to DB.
     */
    @Override
    public PlayerData savePlayer(PlayerData data) {
        data.updateLastUpdated();

        PlayerData playerData = playerDataJpaRepository.save(data);
        UUID joinedMatchId = playerData.getJoinedMatchId();

        // 매치 DB도 갱신
        if (joinedMatchId != null) {
            matchDataJpaRepository.findById(joinedMatchId).ifPresent(matchData -> {
                matchData.updateLastUpdated();
                matchDataJpaRepository.save(matchData);
            });
        }

        return playerData;
    }

    /**
     * Match: delete by ID.
     */
    @Override
    public void deleteMatchById(UUID matchId) {
        matchDataJpaRepository.deleteById(matchId);
    }

    /**
     * Player: delete by ID.
     */
    @Override
    public void deletePlayerById(UUID playerId) {
        playerDataJpaRepository.deleteById(playerId);
    }

    /**
     * Match: Update FRDB.
     */
    @Override
    public void updateFrdbMatchData(MatchData data) {
        frdbRepository.setMatch(data.getId(), data);
    }
}
