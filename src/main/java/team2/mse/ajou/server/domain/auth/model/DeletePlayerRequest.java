package team2.mse.ajou.server.domain.auth.model;

import java.util.UUID;

public record DeletePlayerRequest(
        UUID playerId
) {
}
