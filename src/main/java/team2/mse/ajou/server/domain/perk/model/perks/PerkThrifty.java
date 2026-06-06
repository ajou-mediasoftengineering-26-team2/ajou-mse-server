package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * Perk - 절약가
 * 모든 코인 획득 +2
 * @author Junseo Hwang 202322128
 */
public class PerkThrifty extends Perk {
    private final int bonusValue = 2;

    public PerkThrifty() {
        super(PERK.THRIFTY);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        if (isAttackSuccess(matchData)) {
            DamageData damageData = getCurrentDamageData(matchData);
            damageData.addCoin(bonusValue);
            damageData.addUsedPerk(perk);
            return;
        }

        DefendData defendData = matchData.getDefendData();
        defendData.addCoin(bonusValue);
        defendData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        if (isAttackSuccess(matchData)) {
            DamageData damageData = getCurrentDamageData(matchData);
            return isOwnerAttacker(matchData, ownerPlayerIdx)
                    && damageData != null
                    && damageData.getCoin() > 0;
        }

        DefendData defendData = matchData.getDefendData();
        return isOwnerDefender(matchData, ownerPlayerIdx)
                && defendData != null
                && defendData.getCoin() > 0;
    }
}
