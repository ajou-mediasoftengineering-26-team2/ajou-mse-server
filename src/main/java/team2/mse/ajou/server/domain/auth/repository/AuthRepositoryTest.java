package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Test repository for testing authentication endpoints before migrating to MySQL.
 * DEBUG ONLY!! Will be removed soon.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
@Deprecated(forRemoval = true)
@Qualifier("Test")
public class AuthRepositoryTest implements AuthRepository {
    private Map<String, String> usernameToPlayerToken;
    private Map<String, String> playerTokenToPlayerData;

    public AuthRepositoryTest() {
        this.usernameToPlayerToken = new HashMap<>();
        this.playerTokenToPlayerData = new HashMap<>();
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !usernameToPlayerToken.containsKey(username);
    }

    @Override
    public String login(String username) {

        // 이미 동일한 이름으로 로그인된 상황 (= 닉네임 중복)
        if (usernameToPlayerToken.containsKey(username)) {
            return null;
        }

        String playerId = "PLR_%d".formatted(usernameToPlayerToken.size());
        usernameToPlayerToken.put(username, playerId);
        playerTokenToPlayerData.put(playerId, username);

        return playerId;
    }

    @Override
    public boolean logout(String playerToken) {
        // 주어진 토큰이 로그인이 안된 상황
        if (!playerTokenToPlayerData.containsKey(playerToken)) {
            return false;
        }

        String username = playerTokenToPlayerData.get(playerToken);
        playerTokenToPlayerData.remove(playerToken);
        usernameToPlayerToken.remove(username);
        return true;
    }

    @Override
    public String getPlayerData(String playerToken) {
        // 주어진 토큰이 로그인이 안된 상황
        if (!playerTokenToPlayerData.containsKey(playerToken)) {
            return null;
        }

        String username = playerTokenToPlayerData.get(playerToken);
        return username;
    }
}
