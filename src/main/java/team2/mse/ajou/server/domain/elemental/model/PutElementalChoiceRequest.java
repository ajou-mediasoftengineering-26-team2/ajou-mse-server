package team2.mse.ajou.server.domain.elemental.model;

/**
 * RequestBody DTO for player's elemental choice.
 * @author Junseo Hwang 202322128
 *
 * @param playerId
 * @param handElemental
 */
public record PutElementalChoiceRequest(
        String playerId,
        String handElemental
) {
}
