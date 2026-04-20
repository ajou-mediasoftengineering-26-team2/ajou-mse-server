package team2.mse.ajou.server.domain.auth.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.auth.model.LobbyInfo;
import team2.mse.ajou.server.domain.auth.model.PlayerInfo;
import team2.mse.ajou.server.domain.auth.repository.LobbyInfoRepository;
import team2.mse.ajou.server.domain.auth.repository.PlayerInfoRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class LobbyService {
    private final LobbyInfoRepository lobbyInfoRepository;
    private final PlayerInfoRepository playerInfoRepository;

    public LobbyService(LobbyInfoRepository lobbyInfoRepository, PlayerInfoRepository playerInfoRepository) {
        this.lobbyInfoRepository = lobbyInfoRepository;
        this.playerInfoRepository = playerInfoRepository;
    }

    public LobbyInfo getOpenLobby() {
        return lobbyInfoRepository.findAll()
                .stream()
                .filter(lobby -> lobby.getPlayers().size() < 2)
                .findFirst()
                .orElse(null);
    }
    public LobbyInfo createLobby() {
        LobbyInfo lobbyInfo = new LobbyInfo();
        return lobbyInfoRepository.save(lobbyInfo);
    }
    public LobbyInfo findPlayerLobby(UUID playerId) {
        return lobbyInfoRepository.findAll()
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
        Optional<LobbyInfo> lobbyInfo = lobbyInfoRepository.findById(lobbyId);
        Optional<PlayerInfo> playerInfo = playerInfoRepository.findById(playerId);
        if (lobbyInfo.isEmpty() || playerInfo.isEmpty()) {
            return false;
        }

        LobbyInfo lobby = lobbyInfo.get();
        lobby.getPlayers().add(playerInfo.get());
        lobbyInfoRepository.save(lobby);
        return true;
    }
    public boolean leaveLobby(UUID playerId, UUID lobbyId) {
        Optional<LobbyInfo> lobbyInfo = lobbyInfoRepository.findById(lobbyId);
        Optional<PlayerInfo> playerInfo = playerInfoRepository.findById(playerId);
        if (lobbyInfo.isEmpty() || playerInfo.isEmpty()) {
            return false;
        }

        LobbyInfo newLobbyInfo = lobbyInfo.get();
        if (!newLobbyInfo.getPlayers().remove(playerInfo.get())) {
            return false;
        }

        lobbyInfoRepository.save(lobbyInfo.get());
        return true;
    }
}
