package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * Perk - 강철 주먹
 * 첫 번째 공격 데미지 +2
 * @author Junseo Hwang 202322128
 */
public class PerkIronFist extends Perk {
    private final int bonusValue = 2;

    public PerkIronFist() {
        super(PERK.IRON_FIST);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.addDamage(bonusValue);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && isFirstDamage(damageData);
    }
}
