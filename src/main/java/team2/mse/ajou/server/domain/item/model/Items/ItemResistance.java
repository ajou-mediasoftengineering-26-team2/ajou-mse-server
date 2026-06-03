package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * 저항: HP 20 이하 시 이번 라운드 받는 데미지 -2
 * @author Junseo Hwang 202322128
 */
public class ItemResistance extends ConsumableItem {
    private final int triggerHp = 20;
    private final int reduceDamage = 2;

    private boolean used = false;

    public ItemResistance() {
        super(ITEM_CODE.RESISTANCE);
        used = false;
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        if(!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        damageData.setDamage(Math.max(0, damageData.getDamage() - reduceDamage));
        damageData.addUsedItem(itemCode);
        used = true;
        //이번 턴 모든 데미지 -1 해야해서 일단 소모하지 않음
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && owner != null
                && getCurrentDamageData(matchData) != null
                && hasItem(owner)
                && owner.getHp() <= triggerHp;
    }

    public boolean isUsed() {
        return used;
    }
}
