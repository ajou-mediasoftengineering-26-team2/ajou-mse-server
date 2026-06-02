package team2.mse.ajou.server.domain.turn.model;

/**
 * RequestBody DTO for player's hand choice.
 * @param id player uuid in string format
 * @param choice player's choice
 *               SINGLE_HAND_FLIP_LEFT
 *               SINGLE_HAND_FLIP_RIGHT
 *               BOTH_HANDS_FLIP
 *               INSERT_BETWEEN_HANDS
 *               SHAKE_OVER_HANDS
 *
 * @author Junseo Hwang 202322128
 */
public record PutChoiceRequest(
        String id,
        String choice
) {
}
