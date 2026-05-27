package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemHealingPotion extends ConsumableItem{

    public ItemHealingPotion() {
        super(ITEM_CODE.HEALING_POTION);
    }
    @Override
    public void useItemIfPossible() {

    }
}
