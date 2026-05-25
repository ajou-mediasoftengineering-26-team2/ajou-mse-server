package team2.mse.ajou.server.domain.item.model.Items;

import lombok.Data;
import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 *
 * @author Junseo Hwang 202322128
 */
public class ItemEmergencyTreatment extends ConsumableItem {

    public ItemEmergencyTreatment() {
        super(ITEM_CODE.EMERGENCY_TREATMENT);
    }

    @Override
    public void useItemIfPossible() {

    }

}
