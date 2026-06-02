package team2.mse.ajou.server.domain.elemental.service;

import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;

import java.util.UUID;

/**
 * Elemental 관련 서비스
 * @author Junseo Hwang 202322128
 */
public interface IElementalService {
    /**
     * 플레이어가 선택한 hand elemental을 db에 저장하고 firebase에 올림
     * @param playerId
     * @param handElemental
     */
    void putElementalChoice(UUID playerId, HAND_ELEMENTAL handElemental);
    void receiveElementalAnimationEndAck(UUID playerId);
}
