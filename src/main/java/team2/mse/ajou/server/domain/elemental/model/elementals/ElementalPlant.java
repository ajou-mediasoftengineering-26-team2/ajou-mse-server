package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * 풀 속성 - 일정이상의 데미지를 받으면 일정 데미지 감소
 * Plant
 * If the player takes damage above a certain threshold, reduces the damage by a certain amount.
 * @author Junseo Hwang 202322128
 */
public class ElementalPlant extends Elemental {
    private final int[] damageReduceByLevel = {0, 1, 2, 2, 3, 4};
    private final int[] triggerValueByLevel = {100, 4, 4, 3, 3, 3};

    public ElementalPlant() {
        super(HAND_ELEMENTAL.PLANT);
    }

    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        damageData.setDamage(damageData.getDamage() - damageReduceByLevel[getLevel(owner)]);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && hasElemental(owner)
                && getCurrentDamageData(matchData) != null
                && damageData.getDamage() >= triggerValueByLevel[owner.getElementalLevel()];
    }
}
