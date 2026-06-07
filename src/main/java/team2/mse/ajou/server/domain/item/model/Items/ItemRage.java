package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Item - 분노
 * HP 10 이하 시 다음 첫번째 공격 데미지 +5
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
