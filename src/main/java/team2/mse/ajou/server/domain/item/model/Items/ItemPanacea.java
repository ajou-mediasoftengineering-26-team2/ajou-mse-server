package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemPanacea extends ConsumableItem{

    public ItemPanacea() {
        super(ITEM_CODE.PANACEA);
    }
    @Override
    public void useItemIfPossible() {

    }
}
