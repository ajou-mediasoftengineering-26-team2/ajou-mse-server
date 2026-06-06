package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * 회복제: HP 25 이하 시 즉시 HP +10 회복
 * @author Junseo Hwang 202322128
 */
public class ItemHealingPotion extends ConsumableItem {
    private final int triggerHp = 25;
    private final int healValue = 10;

    public ItemHealingPotion() {
        super(ITEM_CODE.HEALING_POTION);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        heal(owner, damageData, healValue);
        consumeItem(owner, damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        return isInTurn(matchData)
                && owner != null
                && getCurrentDamageData(matchData) != null
                && hasItem(owner)
                && owner.getHp() <= triggerHp;
    }
}
