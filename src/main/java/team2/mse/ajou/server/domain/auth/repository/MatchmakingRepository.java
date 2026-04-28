package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface MatchmakingRepository {
    String getOpenLobby();
    String createLobby();
    String findPlayerLobby(String playerToken);
    boolean joinLobby(String playerToken, String lobbyId);
    boolean leaveLobby(String playerToken, String lobbyId);
}
