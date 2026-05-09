package team2.mse.ajou.server.domain.auth.model;

/**
 * Response DTO for username availability.
 * @param isAvailable whether the username is available.
 *
 * @author Ahn Yubin / 202021088
 */
public record GetPlayerResponse(
        boolean isAvailable
) {
}
