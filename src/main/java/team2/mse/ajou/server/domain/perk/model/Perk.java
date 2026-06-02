package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.PERK;

public abstract class Perk implements IPerk {
    protected final PERK perk;

    public Perk(PERK perk) {
        this.perk = perk;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
