package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * 분노: HP 10 이하 시 다음 공격 데미지 +5
 * 현재 데미지 구조에서는 다중 타격 공격의 첫 번째 damageData에만 적용합니다.
 * @author Junseo Hwang 202322128
 */
public class ItemRage extends ConsumableItem {
    private final int triggerHp = 10;
    private final int bonusDamage = 5;

    public ItemRage() {
        super(ITEM_CODE.RAGE);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        damageData.addDamage(bonusDamage);
        consumeItem(owner, damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && owner != null
                && damageData != null
                && isFirstDamage(damageData)
                && hasItem(owner)
                && owner.getHp() <= triggerHp;
    }
}
