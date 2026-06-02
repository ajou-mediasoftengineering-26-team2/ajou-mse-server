package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public class PerkIronFist extends Perk {
    private final int bonusValue = 2;

    public PerkIronFist() {
        super(PERK.IRON_FIST);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.addDamage(bonusValue);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        if(!isInTurn(matchData)) return false;
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData) && isFirstDamage(damageData);
    }
}
