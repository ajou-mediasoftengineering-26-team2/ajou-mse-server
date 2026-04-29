package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.auth.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.player.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.firebase.service.FrdbMatchService;
import team2.mse.ajou.server.domain.shared.match.MatchState;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.player.model.PlayerData;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 로비 (매치메이킹) 관리 서비스.
 *
 * @author yubin
 */
@Service
public class MatchService {
    private final FrdbMatchService frdbMatchService;
    private final MatchDataRepository matchDataRepository;
    private final PlayerDataRepository playerDataRepository;

    public MatchService(FrdbMatchService frdbMatchService, MatchDataRepository matchDataRepository, PlayerDataRepository playerDataRepository) {
        this.frdbMatchService = frdbMatchService;
        this.matchDataRepository = matchDataRepository;
        this.playerDataRepository = playerDataRepository;
    }

    public MatchData getOpenMatch() {
        return matchDataRepository.findAll()
                .stream()
                .filter(match -> match.getPlayers().size() < 2)
                .findFirst()
                .orElse(null);
    }

    public MatchData createMatch() {
        // DB에 저장
        MatchData matchData = new MatchData();
        matchData = matchDataRepository.save(matchData);

        System.out.println("LOBBY CREATE: %s / %s".formatted(matchData.getId(), matchData.getPlayers()));

        return matchData;
    }

    public MatchData findMatchByPlayerId(UUID playerId) {
        return matchDataRepository.findAll()
                .stream()
                .filter(lobby ->
                        lobby
                                .getPlayers()
                                .stream()
                                .anyMatch(player -> player.getId().equals(playerId))
                )
                .findFirst()
                .orElse(null);
    }

    public boolean joinMatch(UUID playerId, UUID matchId) {
        Optional<MatchData> matchData = matchDataRepository.findById(matchId);
        Optional<PlayerData> playerData = playerDataRepository.findById(playerId);
        if (matchData.isEmpty() || playerData.isEmpty()) {
            return false;
        }

        MatchData newMatchData = matchData.get();

        System.out.println("MATCH JOIN: %s / %s".formatted(newMatchData.getId(), newMatchData.getPlayers()));

        newMatchData.getPlayers().add(playerData.get());
        newMatchData.setCountdownStartTime(ZonedDateTime.now());

        if (newMatchData.getPlayers().size() >= 2) {
            newMatchData.setCountdownSec(20);
        }

        newMatchData = matchDataRepository.save(newMatchData);

        // Firebase RDB속 로비에 플레이어 추가
        frdbMatchService.setMatch(matchId, newMatchData);
        return true;
    }

    public boolean leaveMatch(UUID playerId, UUID matchId) {
        Optional<MatchData> lobbyData = matchDataRepository.findById(matchId);
        Optional<PlayerData> playerData = playerDataRepository.findById(playerId);
        if (lobbyData.isEmpty() || playerData.isEmpty()) {
            return false;
        }

        MatchData newMatchData = lobbyData.get();
        if (!newMatchData.getPlayers().remove(playerData.get())) {
            return false;
        }

        if (newMatchData.getPlayers().isEmpty()) {
            newMatchData.setState(MatchState.PLAYER_DISCONNECTED_ALL);
        } else if (newMatchData.getState() != MatchState.WAITING && newMatchData.getState() != MatchState.PLAYER_DISCONNECTED_ALL) {
            newMatchData.setState(MatchState.PLAYER_DISCONNECTED);
        }
        newMatchData.setCountdownStartTime(ZonedDateTime.now());

        newMatchData = matchDataRepository.save(newMatchData);

        // Firebase RDB속 로비에 플레이어 제거
        frdbMatchService.setMatch(matchId, newMatchData);
        return true;
    }
}
