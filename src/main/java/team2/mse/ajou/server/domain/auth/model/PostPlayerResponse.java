package team2.mse.ajou.server.domain.auth.model;

import java.util.UUID;

/**
 * Response DTO for player login.
 * @param playerId Created player UUID.
 * @param matchId Joined / created match UUID.
 *
 * @author Ahn Yubin / 202021088
 */
public record PostPlayerResponse(
        UUID playerId,
        UUID matchId
) {
}
