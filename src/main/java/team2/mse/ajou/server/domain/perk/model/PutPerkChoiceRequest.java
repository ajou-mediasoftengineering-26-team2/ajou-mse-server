package team2.mse.ajou.server.domain.perk.model;

/**
 * @author Junseo Hwang 202322128
 * @param playerId
 * @param perk
 */
public record PutPerkChoiceRequest(
        String playerId,
        String perk
) {
}
