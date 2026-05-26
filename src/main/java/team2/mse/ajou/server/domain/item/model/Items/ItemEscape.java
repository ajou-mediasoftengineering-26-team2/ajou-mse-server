package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemEscape extends ConsumableItem{

    public ItemEscape() {
        super(ITEM_CODE.ESCAPE);
    }
    @Override
    public void useItemIfPossible() {

    }
}
