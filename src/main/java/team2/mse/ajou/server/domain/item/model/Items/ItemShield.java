package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Item - 보호막
 * 보호막: HP 20 이하 시 받는 데미지 0
 * set received damage == 0 HP when HP is 20 or lower.
 * @author Junseo Hwang 202322128
 */
public class ItemShield extends ConsumableItem {
    private final int triggerHp = 20;

    public ItemShield() {
        super(ITEM_CODE.SHIELD);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        damageData.setDamage(0);
        consumeItem(owner, damageData);
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
}
