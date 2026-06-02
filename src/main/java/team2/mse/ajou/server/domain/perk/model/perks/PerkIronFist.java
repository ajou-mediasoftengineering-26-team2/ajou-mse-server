package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public class PerkIronFist extends Perk {
    private final int bonusValue = 2;

    public PerkIronFist() {
        super(PERK.IRON_FIST);
    }

    @Override
    public void usePerkIfPossible(PlayerData player, DamageData damageData) {
        if(isAvailable(player, damageData)) {
            damageData.addDamage(bonusValue);
            damageData.addUsedPerk(perk);
        }
    }

    @Override
    public boolean isAvailable(PlayerData player, DamageData damageData)
    {
        return damageData.getDamageIndex() == 0;
    }
}
