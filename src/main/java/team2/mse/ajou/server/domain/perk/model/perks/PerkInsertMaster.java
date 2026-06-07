package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 찌르기 달인
 * 찌르기로 공격 성공시 첫번째 데미지 +2
 * @author Junseo Hwang 202322128
 */
public class PerkInsertMaster extends Perk {
    private final int bonusValue = 2;

    public PerkInsertMaster() {
        super(PERK.INSERT_MASTER);
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

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && owner != null
                && owner.getChoice() == HAND_CHOICE.INSERT_BETWEEN_HANDS
                && isFirstDamage(damageData);
    }
}
