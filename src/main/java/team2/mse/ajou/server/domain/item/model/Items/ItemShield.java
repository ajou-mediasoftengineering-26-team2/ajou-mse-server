package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemShield extends ConsumableItem {

    public ItemShield() {
        super(ITEM_CODE.SHIELD);
    }

    @Override
    public void useItemIfPossible() {

    }

}