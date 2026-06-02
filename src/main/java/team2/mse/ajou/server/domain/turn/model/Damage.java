package team2.mse.ajou.server.domain.turn.model;

import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;
import team2.mse.ajou.server.domain.turn.STATUS_EFFECT;

import java.util.List;

public record Damage(
        int damage,
        int coin,
        int recoveredHp,
        ATTACK_TYPE attackType,
        List<ITEM_CODE> usedItems,
        List<PERK> usedPerks,
        List<STATUS_EFFECT> statusEffects,
        boolean isKO
) {
}
