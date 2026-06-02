package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkInsertMaster extends Perk {
    private final int bonusValue = 2;

    public PerkInsertMaster() {
        super(PERK.INSERT_MASTER);
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
                && attacker.getChoice() == HAND_CHOICE.INSERT_BETWEEN_HANDS
                && isFirstDamage(damageData);
    }
}
