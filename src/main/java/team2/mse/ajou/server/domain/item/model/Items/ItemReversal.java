package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemReversal extends ConsumableItem {

    public ItemReversal() {
        super(ITEM_CODE.REVERSAL);
    }
    @Override
    public void useItemIfPossible() {

    }
}
