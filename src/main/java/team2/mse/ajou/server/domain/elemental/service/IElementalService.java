package team2.mse.ajou.server.domain.elemental.service;

import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;

import java.util.UUID;

/**
 * Elemental 관련 서비스
 * Elemental Service
 * @author Junseo Hwang 202322128
 */
public interface IElementalService {
    /**
     * player의 elemental 선택을 저장함
     * This save player's elemental choice
     * @param playerId uuid of player that has chosen elemental
     * @param handElemental hand elemental enum that chosen by player
     */
    void putElementalChoice(UUID playerId, HAND_ELEMENTAL handElemental);
    /**
     * player의 elemental upgrade 요청을 처리함
     * This operates player's elemental upgrade request
     * @param playerId uuid of player that has sent upgrade request
     * @param handElemental player's hand elemental
     */
    void upgradeElemental(UUID playerId, HAND_ELEMENTAL handElemental);
    /**
     * ELEMENTAL_RECEIVE_ANIMATION_END ack를 저장함
     * This save ELEMENTAL_RECEIVE_ANIMATION_END ACK
     * @param playerId player that has sent ELEMENTAL_RECEIVE_ANIMATION_END ACK
     */
    void receiveElementalAnimationEndAck(UUID playerId);
}
