package team2.mse.ajou.server.domain.shared;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.auth.repository.LobbyDataRepository;
import team2.mse.ajou.server.domain.auth.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.firebase.lobby.FrdbLobbyService;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 로비 (매치메이킹) 관리 서비스.
 *
 * @author yubin
 */
@Service
public class LobbyService {
    private final FrdbLobbyService frdbLobbyService;
    private final LobbyDataRepository lobbyDataRepository;
    private final PlayerDataRepository playerDataRepository;

    public LobbyService(FrdbLobbyService frdbLobbyService, LobbyDataRepository lobbyDataRepository, PlayerDataRepository playerDataRepository) {
        this.frdbLobbyService = frdbLobbyService;
        this.lobbyDataRepository = lobbyDataRepository;
        this.playerDataRepository = playerDataRepository;
    }

    public LobbyData getOpenLobby() {
        return lobbyDataRepository.findAll()
                .stream()
                .filter(lobby -> lobby.getPlayers().size() < 2)
                .findFirst()
                .orElse(null);
    }

    public LobbyData createLobby() {
        // DB에 저장
        LobbyData lobbyData = new LobbyData();
        lobbyData = lobbyDataRepository.save(lobbyData);

        System.out.println("LOBBY CREATE: %s / %s".formatted(lobbyData.getId(), lobbyData.getPlayers()));

        return lobbyData;
    }

    public LobbyData findLobbyByPlayerId(UUID playerId) {
        return lobbyDataRepository.findAll()
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

    public boolean joinLobby(UUID playerId, UUID lobbyId) {
        Optional<LobbyData> lobbyData = lobbyDataRepository.findById(lobbyId);
        Optional<PlayerData> playerData = playerDataRepository.findById(playerId);
        if (lobbyData.isEmpty() || playerData.isEmpty()) {
            return false;
        }

        LobbyData newLobbyData = lobbyData.get();

        System.out.println("LOBBY JOIN: %s / %s".formatted(newLobbyData.getId(), newLobbyData.getPlayers()));

        newLobbyData.getPlayers().add(playerData.get());
        newLobbyData.setCountdownStartTime(ZonedDateTime.now());

        if (newLobbyData.getPlayers().size() >= 2) {
            newLobbyData.setCountdownSec(20);
        }

        newLobbyData = lobbyDataRepository.save(newLobbyData);

        // Firebase RDB속 로비에 플레이어 추가
        frdbLobbyService.setLobby(lobbyId, newLobbyData);
        return true;
    }

    public boolean leaveLobby(UUID playerId, UUID lobbyId) {
        Optional<LobbyData> lobbyData = lobbyDataRepository.findById(lobbyId);
        Optional<PlayerData> playerData = playerDataRepository.findById(playerId);
        if (lobbyData.isEmpty() || playerData.isEmpty()) {
            return false;
        }

        LobbyData newLobbyData = lobbyData.get();
        if (!newLobbyData.getPlayers().remove(playerData.get())) {
            return false;
        }

        if (newLobbyData.getPlayers().isEmpty()) {
            newLobbyData.setState(LobbyState.PLAYER_DISCONNECTED_ALL);
        } else if (newLobbyData.getState() != LobbyState.WAITING && newLobbyData.getState() != LobbyState.PLAYER_DISCONNECTED_ALL) {
            newLobbyData.setState(LobbyState.PLAYER_DISCONNECTED);
        }
        newLobbyData.setCountdownStartTime(ZonedDateTime.now());

        newLobbyData = lobbyDataRepository.save(newLobbyData);

        // Firebase RDB속 로비에 플레이어 제거
        frdbLobbyService.setLobby(lobbyId, newLobbyData);
        return true;
    }
}
