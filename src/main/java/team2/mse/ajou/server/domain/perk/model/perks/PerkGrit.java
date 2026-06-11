package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 근성
 * 체력이 15 이하일때 받는 데미지 절반(올림)
 * receiving damage is half(ceiling) when HP is 15 or lower.
 * @author Junseo Hwang 202322128
 */
public class PerkGrit extends Perk {
    private final int triggerHp = 15;

    public PerkGrit() {
        super(PERK.GRIT);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        int prevDamage = damageData.getDamage();
        damageData.setDamage((prevDamage + 1) / 2);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && owner != null
                && damageData != null
                && damageData.getDamage() > 0
                && owner.getHp() <= triggerHp;
    }
}
