package team2.mse.ajou.server.domain.round.service;

import java.util.UUID;

/**
 * Round Service
 * @author Junseo Hwang 202322128
 */
public interface IRoundService {
    /**
     * ROUND_START_ANIMATION_END ack를 저장함
     * This save ROUND_START_ANIMATION_END ACK
     * @param playerId uuid of player that has sent ROUND_START_ANIMATION_END ACK
     */
    public void receiveRoundStart(UUID playerId);
    /**
     * ROUND_END_ANIMATION_END ack를 저장함
     * This save ROUND_END_ANIMATION_END ACK
     * @param playerId uuid of player that has sent ROUND_END_ANIMATION_END ACK
     */
    public void receiveRoundEnd(UUID playerId);
}
