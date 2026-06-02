package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;

public class PerkFlexibleMind extends Perk {
    public PerkFlexibleMind() {
        super(PERK.FLEXIBLE_MIND);
    }

    @Override
    public void usePerkIfPossible() {

    }

    @Override
    public boolean isAvailable()
    {
        return true;
    }
}
