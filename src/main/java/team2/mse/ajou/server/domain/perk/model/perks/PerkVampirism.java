package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;

public class PerkVampirism extends Perk {
    private final int healValue = 3;

    public PerkVampirism() {
        super(PERK.VAMPIRISM);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.addRecoveredHp(healValue);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        if(!isInTurn(matchData)) return false;
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData) && isFirstDamage(damageData);
    }
}
