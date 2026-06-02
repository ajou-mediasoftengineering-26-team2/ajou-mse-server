package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;

public class PerkFlexibleHands extends Perk {
    public PerkFlexibleHands() {
        super(PERK.FLEXIBLE_HANDS);
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
