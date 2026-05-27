package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemResistance extends ConsumableItem {

    public ItemResistance() {
        super(ITEM_CODE.RESISTANCE);
    }
    @Override
    public void useItemIfPossible() {

    }
}
