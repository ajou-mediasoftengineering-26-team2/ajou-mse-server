package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public class PerkVampirism extends Perk {
    private final int bonusValue = 3;
    public PerkVampirism() {
        super(PERK.VAMPIRISM);
    }

    @Override
    public void usePerkIfPossible(PlayerData player, DamageData damageData) {
        if(isAvailable(player, damageData)) {
            damageData.addRecoveredHp(bonusValue);
            damageData.addUsedPerk(perk);
        }
    }

    @Override
    public boolean isAvailable(PlayerData player, DamageData damageData)
    {
        return !isUsed;
    }
}
