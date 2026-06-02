package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public class PerkUnyielding extends Perk {
    private final int maxDamage = 5;

    public PerkUnyielding() {
        super(PERK.UNYIELDING);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.setDamage(maxDamage);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData) && damageData != null && damageData.getDamage() > maxDamage;
    }
}
