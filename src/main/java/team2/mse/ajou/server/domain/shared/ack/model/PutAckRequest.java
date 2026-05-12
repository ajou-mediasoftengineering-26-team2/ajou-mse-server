package team2.mse.ajou.server.domain.shared.ack.model;

/**
 * Request DTO for Ack.
 * @param id Player UUID.
 * @param ackType Ack type
 *                DEFAULT
 *                CUT_SCENE_END
 *                TURN_ANIMATION_END
 *                ROUND_END
 *                SHOP_END
 *                ITEM_RECEIVE_ANIMATION
 *                MATCH_END
 *
 * @author Junseo Hwang 202322128
 */
public record PutAckRequest(
        String id,
        String ackType
) {
}
