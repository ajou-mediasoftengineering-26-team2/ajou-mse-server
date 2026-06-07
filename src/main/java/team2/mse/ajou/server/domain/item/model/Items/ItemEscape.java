package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Item - 도주
 * HP 5 이하 시 즉시 HP +10, 코인 +5
 * @author Junseo Hwang 202322128
 */
public class ItemEscape extends ConsumableItem {
    private final int triggerHp = 5;
    private final int healValue = 10;
    private final int coinValue = 5;

    public ItemEscape() {
        super(ITEM_CODE.ESCAPE);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        heal(owner, healValue);
        owner.setCoin(owner.getCoin() + coinValue);
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
