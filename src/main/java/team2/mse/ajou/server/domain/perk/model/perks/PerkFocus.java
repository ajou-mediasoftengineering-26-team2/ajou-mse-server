package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 집중
 * 가만히로 공격 성공 시 첫번째 공격 데미지 +3
 * first attack damage +3 when attack success with SHAKE_OVER_HANDS
 */
public class PerkFocus extends Perk {
    private final int bonusValue = 3;

    public PerkFocus() {
        super(PERK.FOCUS);
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
                && owner.getChoice() == HAND_CHOICE.SHAKE_OVER_HANDS
                && isFirstDamage(damageData);
    }
}
