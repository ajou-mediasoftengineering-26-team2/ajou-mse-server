package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.firebase.service.IFrdbRepository;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 게임 데이터 총괄하는 리포지토리.
 * 기존의 MatchData, PlayerData, FRDB를 따로 관리하니 생기는 이슈인 동시성 문제 등을 어느정도 완화시키기 위해 하나의 통일된 데이터 창구에서 관리하게 만들었습니다.
 * 내부적으로 (MySQL) DB에 접근하는 JPA 리포지토리, FRDB를 접근하는 리포지토리를 모두 사용합니다.
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

    @Override
    public Optional<MatchData> findMatchById(UUID matchId) {
        return matchDataJpaRepository.findById(matchId);
    }

    @Override
    public Optional<MatchData> findMatchByJoinedPlayerId(UUID playerId) {
        PlayerData playerData = playerDataJpaRepository.findById(playerId).orElse(null);
        if (playerData == null) {
            return Optional.empty();
        }

        return matchDataJpaRepository.findById(playerData.getJoinedMatchId());
    }

    @Override
    public List<MatchData> findAllMatches() {
        return matchDataJpaRepository.findAll();
    }

    @Override
    public Optional<PlayerData> findPlayerById(UUID playerId) {
        return playerDataJpaRepository.findById(playerId);
    }

    @Override
    public boolean isPlayerExistsByUsername(String name) {
        return playerDataJpaRepository.existsByUsername(name);
    }

    @Override
    public boolean isPlayerExistsById(UUID playerId) {
        return playerDataJpaRepository.existsById(playerId);
    }

    @Override
    public MatchData saveMatch(MatchData data) {
        data.updateLastUpdated();

        MatchData matchData = matchDataJpaRepository.save(data);

        // 플레이어 DB도 갱신
        playerDataJpaRepository.saveAll(matchData.getPlayers());

        return matchDataJpaRepository.save(data);
    }

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

    @Override
    public void deleteMatchById(UUID matchId) {
        matchDataJpaRepository.deleteById(matchId);
    }

    @Override
    public void deletePlayerById(UUID playerId) {
        playerDataJpaRepository.deleteById(playerId);
    }

    @Override
    public void updateFrdbMatchData(MatchData data) {
        frdbRepository.setMatch(data.getId(), data);
    }
}
