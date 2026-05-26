package team2.mse.ajou.server.domain.elemental.service;

import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;

import java.util.UUID;

public interface IElementalService {
    void putElementalChoice(UUID playerId, HAND_ELEMENTAL handElemental);
    void receiveElementalAnimationEndAck(UUID playerId);
}
