package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkGrit extends Perk {
    private final int triggerHp = 15;

    public PerkGrit() {
        super(PERK.GRIT);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        int prevDamage = damageData.getDamage();
        damageData.setDamage((prevDamage + 1) / 2);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        if(!isInTurn(matchData)) return false;
        
        PlayerData defender = getDefender(matchData);
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && defender != null
                && damageData != null
                && damageData.getDamage() > 0
                && defender.getHp() <= triggerHp;
    }
}
