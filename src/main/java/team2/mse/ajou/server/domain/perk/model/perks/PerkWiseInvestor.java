package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * Perk - 현명한 투자가
 * 보유 코인 10당 첫번째 공격 데미지 +1
 * @author Junseo Hwang 202322128
 */
public class PerkWiseInvestor extends Perk {
    private final int moduloValue = 10;

    public PerkWiseInvestor() {
        super(PERK.WISE_INVESTOR);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        damageData.addDamage(owner.getCoin() / moduloValue);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && owner != null
                && damageData != null
                && isFirstDamage(damageData)
                && owner.getCoin() >= moduloValue;
    }
}
