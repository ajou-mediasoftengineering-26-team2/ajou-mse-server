package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkFocus extends Perk {
    private final int bonusValue = 3;

    public PerkFocus() {
        super(PERK.FOCUS);
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
        
        PlayerData attacker = getAttacker(matchData);
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && attacker != null
                && attacker.getChoice() == HAND_CHOICE.SHAKE_OVER_HANDS
                && isFirstDamage(damageData);
    }
}
