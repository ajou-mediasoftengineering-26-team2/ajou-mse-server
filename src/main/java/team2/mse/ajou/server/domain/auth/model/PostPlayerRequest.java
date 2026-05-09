package team2.mse.ajou.server.domain.auth.model;

/**
 * Request DTO for player login.
 * @param playerName Username.
 *
 * @author Ahn Yubin / 202021088
 */
public record PostPlayerRequest(
        String playerName
) {
}
