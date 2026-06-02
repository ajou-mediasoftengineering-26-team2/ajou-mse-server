package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public class PerkLuck extends Perk {
    public PerkLuck() {
        super(PERK.LUCK);
    }

    @Override
    public void usePerkIfPossible(PlayerData player, DamageData damageData) {

    }

    @Override
    public boolean isAvailable(PlayerData player, DamageData damageData)
    {
        return true;
    }
}
