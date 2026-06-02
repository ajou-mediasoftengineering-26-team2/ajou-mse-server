package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;

public class PerkVampirism extends Perk {
    public PerkVampirism() {
        super(PERK.VAMPIRISM);
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
