package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public abstract class Perk implements IPerk {
    protected final PERK perk;
    protected boolean isUsed;

    public Perk(PERK perk) {
        this.perk = perk;
    }

    @Override
    public boolean isAvailable(PlayerData playerData, DamageData damageData) {
        return false;
    }
}
