package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * 독 속성 - 공격시 독 상태이상을 부여
 * 독 상태이상: 독 상태에서 공격시 데미지 일정량 감소
 * Poison elemental
 * Applies the Poison status effect on attack.
 * Poison status effect: While poisoned, attack damage is reduced by a certain amount.
 *
 * @author Junseo Hwang 202322128
 */
public class ElementalPoison extends Elemental {
    private final int[] damageReduceByLevel = {0, 1, 2, 3, 4, 5};

    public ElementalPoison() {
        super(HAND_ELEMENTAL.POISON);
    }

    // 공격, 수비가 나눠져 있습니다.
    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);

        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        int level = getLevel(owner);

        // 내가 공격했을 때
        if(isOwnerAttacker(matchData, ownerPlayerIdx)) {
            addUsedElemental(damageData);
            if(opponent.getStatusEffectList() != null
                    && !opponent.getStatusEffectList().contains(STATUS_EFFECT.POISON))
            {
                opponent.getStatusEffectList().add(STATUS_EFFECT.POISON);
            }
            return;
        }

        // 내가 맞았는데 상대가 Poison 상태일때
        if(isOwnerDefender(matchData, ownerPlayerIdx)
                && opponent.getStatusEffectList() != null
                && opponent.getStatusEffectList().contains(STATUS_EFFECT.POISON))
        {
            // 일단 여기서 0로 막지 않음. elemental은 perk, item보다 먼저 계산되기 때문에 perk, item에서 lower bound가 걸림
            damageData.setDamage(damageData.getDamage()-damageReduceByLevel[level]);
        }
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && hasElemental(owner)
                && opponent != null
                && damageData != null
                && isFirstDamage(damageData);
    }
}
