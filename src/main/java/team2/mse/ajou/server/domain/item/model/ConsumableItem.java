package team2.mse.ajou.server.domain.item.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
@Data
public abstract class ConsumableItem implements IConsumableItem {
    protected final ITEM_CODE itemCode;

    public ConsumableItem(ITEM_CODE itemCode) {
        this.itemCode = itemCode;
    }



    @Override
    public boolean isAvailable() {
        return  true;
    }
}
