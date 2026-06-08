package team2.mse.ajou.server.domain.auth.service;

import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.domain.auth.model.LoginAndJoinResult;

import java.util.UUID;

/**
 * Player login & matchmaking related service.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IAuthService {
    /**
     * Login with given username then joins (and creates if needed) a match.
     * Returns player and match (UU)ID.
     *
     * @param playerName Player username.
     * @return Result data.
     */
    @Transactional
    LoginAndJoinResult loginAndJoin(String playerName);

    /**
     * Logs out player from given player UUID. Leaves ongoing match if the player is currently joining one.
     *
     * @param playerId Player UUID.
     */
    @Transactional
    void logout(UUID playerId);

    /**
     * Checks whether given username is available.
     *
     * @param playerName Username.
     * @return Whether given username is available.
     */
    boolean checkPlayerNameAvailable(String playerName);
}
