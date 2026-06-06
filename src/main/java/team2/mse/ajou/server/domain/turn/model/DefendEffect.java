package team2.mse.ajou.server.domain.turn.model;

import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.PERK;

public record DefendEffect(
        int recoveredHp,
        int coin,
        PERK usedPerk,
        HAND_ELEMENTAL usedElemental
) {
}
