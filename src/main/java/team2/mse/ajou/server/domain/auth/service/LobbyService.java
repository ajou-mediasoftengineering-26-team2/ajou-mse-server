package team2.mse.ajou.server.domain.auth.service;

import com.google.firebase.database.FirebaseDatabase;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.LobbyData;
import team2.mse.ajou.server.domain.shared.PlayerData;
import team2.mse.ajou.server.domain.auth.repository.LobbyDataRepository;
import team2.mse.ajou.server.domain.auth.repository.PlayerDataRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 로비 (매치메이킹) 관리 서비스.
 *
 * @author yubin
 */
@Service
public class LobbyService {
    private final FirebaseDatabase firebaseDatabase;
    private final LobbyDataRepository lobbyDataRepository;
    private final PlayerDataRepository playerDataRepository;

    public LobbyService(FirebaseDatabase firebaseDatabase, LobbyDataRepository lobbyDataRepository, PlayerDataRepository playerDataRepository) {
        this.firebaseDatabase = firebaseDatabase;
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

        // Firebase RDB에 로비 생성
        firebaseDatabase
                .getReference("lobbies")
                .child(lobbyData.getId().toString())
                .push();

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

        LobbyData lobby = lobbyData.get();

        System.out.println("LOBBY JOIN: %s / %s".formatted(lobby.getId(), lobby.getPlayers()));

        // Firebase RDB속 로비에 플레이어 추가
        firebaseDatabase
                .getReference("lobbies")
                .child(lobbyId.toString())
                .push();

        lobby.getPlayers().add(playerData.get());
        lobbyDataRepository.save(lobby);
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

        lobbyDataRepository.save(lobbyData.get());
        return true;
    }
}
