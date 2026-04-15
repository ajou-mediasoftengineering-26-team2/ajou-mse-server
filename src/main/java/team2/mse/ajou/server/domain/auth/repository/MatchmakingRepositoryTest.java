package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.apiresponse.model.ApiError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MatchmakingRepositoryTest implements MatchmakingRepository {
    private Map<String, String> playerTokenToLobby;
    private Map<String, List<String>> lobbyToPlayers;

    public MatchmakingRepositoryTest() {
        this.lobbyToPlayers = new HashMap<>();
        this.playerTokenToLobby = new HashMap<>();
    }

    @Override
    public String getOpenLobby() {
        return lobbyToPlayers
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() == 1) // 1명 플레이어만 있는 경우
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String createLobby() {
        String lobbyId = "LOBBY_%d".formatted(lobbyToPlayers.size());
        List<String> playersList = new ArrayList<>();

        lobbyToPlayers.put(lobbyId, playersList);

        return lobbyId;
    }

    @Override
    public String findPlayerLobby(String playerToken) {
        return playerTokenToLobby.getOrDefault(playerToken, null);
    }

    @Override
    public boolean joinLobby(String playerToken, String lobbyId) {
        if (!lobbyToPlayers.containsKey(lobbyId)) {
            return false;
        }

        List<String> playersList = lobbyToPlayers.get(lobbyId);
        playersList.add(playerToken);

        playerTokenToLobby.put(playerToken, lobbyId);
        return true;
    }

    @Override
    public boolean leaveLobby(String playerToken, String lobbyId) {
        if (!lobbyToPlayers.containsKey(lobbyId)) {
            return false;
        }

        List<String> playersList = lobbyToPlayers.get(lobbyId);
        playersList.remove(playerToken);

        playerTokenToLobby.remove(playerToken);
        return true;
    }
}
