package team2.mse.ajou.server.domain.auth.model;

/**
 * Request DTO for username availability.
 * @param username Username to check.
 *
 * @author Ahn Yubin / 202021088
 */
public record GetPlayerRequest(
        String username
) {
}
