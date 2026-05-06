package team2.mse.ajou.server.domain.auth.model;

import java.util.UUID;

/**
 * Request DTO for logout.
 * @param playerId Player UUID.
 *
 * @author Ahn Yubin / 202021088
 */
public record DeletePlayerRequest(
        UUID playerId
) {
}
