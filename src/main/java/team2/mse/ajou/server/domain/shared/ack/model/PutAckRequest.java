package team2.mse.ajou.server.domain.shared.ack.model;

/**
 * Request DTO for Ack.
 * @param playerId Player UUID.
 *
 * @author Junseo Hwang 202322128
 */
public record PutAckRequest(
        String playerId
) {
}
