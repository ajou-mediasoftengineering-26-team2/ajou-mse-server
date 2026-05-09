package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.stereotype.Repository;

/**
 * Test repository for testing authentication endpoints before migrating to MySQL.
 * DEBUG ONLY!! Will be removed soon.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
@Deprecated(forRemoval = true)
public interface AuthRepository {
    boolean isUsernameAvailable(String username);

    String login(String username);
    boolean logout(String playerToken);

    String getPlayerData(String playerToken);
}
