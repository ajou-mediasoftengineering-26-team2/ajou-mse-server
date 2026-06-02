package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public class PerkGrit extends Perk {
    private final int triggerValue = 15;

    public PerkGrit() {
        super(PERK.GRIT);
    }

    @Override
    public void usePerkIfPossible(PlayerData player, DamageData damageData) {
        if(isAvailable(player, damageData)) {
            int prevDamage = damageData.getDamage();
            // 2로 나누고 올림 한거에요
            damageData.setDamage((prevDamage+1)/2);
            damageData.addUsedPerk(perk);
        }
    }

    @Override
    public boolean isAvailable(PlayerData player, DamageData damageData)
    {
        return player.getHp() - damageData.getDamage() <= triggerValue;
    }
}
