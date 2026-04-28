package team2.mse.ajou.server.domain.auth.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository {
    boolean isUsernameAvailable(String username);

    String login(String username);
    boolean logout(String playerToken);

    String getPlayerData(String playerToken);
}
