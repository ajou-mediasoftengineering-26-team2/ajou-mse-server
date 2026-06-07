package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Item - 최후의 일격
 * HP 5 이하 시 다음 첫번째 공격 데미지 2배
 * @author Junseo Hwang 202322128
 */
public class ItemFinalBlow extends ConsumableItem {
    private final int triggerHp = 5;
    private final int multiplier = 2;

    public ItemFinalBlow(){
        super(ITEM_CODE.FINAL_BLOW);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        damageData.setDamage(damageData.getDamage() * multiplier);
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
                && damageData.getDamage() > 0
                && isFirstDamage(damageData)
                && hasItem(owner)
                && owner.getHp() <= triggerHp;
    }
}
