package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * Perk - 불굴
 * 한 번에 받는 최대 데미지 5로 제한
 * Limits the maximum damage received at once to 5.
 * @author Junseo Hwang 202322128
 */
public class PerkUnyielding extends Perk {
    private final int maxDamage = 5;

    public PerkUnyielding() {
        super(PERK.UNYIELDING);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.setDamage(maxDamage);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && damageData != null
                && damageData.getDamage() > maxDamage;
    }
}
