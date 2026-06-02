package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public class PerkThrifty extends Perk {
    private final int bonusValue = 2;

    public PerkThrifty() {
        super(PERK.THRIFTY);
    }

    @Override
    public void usePerkIfPossible(PlayerData player, DamageData damageData) {
        if(isAvailable(player, damageData)) {
            damageData.addCoin(bonusValue);
            damageData.addUsedPerk(perk);
        }
    }

    @Override
    public boolean isAvailable(PlayerData player, DamageData damageData)
    {
        return damageData.getCoin() > 0;
    }
}
