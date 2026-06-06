package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;

/**
 * 불 속성 - 공격시 Burning 상태이상. 턴이 종료될 때 Burning상태라면 데미지를 입음
 * @author Junseo Hwang 202322128
 */
public class ElementalFire extends Elemental {
    private final int[] burnDamageByLevel = {0, 1, 2, 3, 4, 5};

    public ElementalFire() {
        super(HAND_ELEMENTAL.FIRE);
    }

    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        int level = getLevel(owner);

        // 불 도트딜 결정단계
        if (damageData.getAttackType() == ATTACK_TYPE.BURNING) {
            damageData.setDamage(damageData.getDamage() + burnDamageByLevel[level]);
            addUsedElemental(damageData);
            return;
        }

        // 첫 타격 Burning 상태이상 부여 단계
        if(isFirstDamage(damageData)) {
            if (opponent.getStatusEffectList() != null && !opponent.getStatusEffectList().contains(STATUS_EFFECT.BURNING)) {
                opponent.getStatusEffectList().add(STATUS_EFFECT.BURNING);
            }
            damageData.addStatusEffect(STATUS_EFFECT.BURNING);
            addUsedElemental(damageData);
        }
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && hasElemental(owner)
                && opponent != null
                && damageData != null;
    }
}
