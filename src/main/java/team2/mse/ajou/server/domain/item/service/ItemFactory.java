package team2.mse.ajou.server.domain.item.service;

import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.item.model.Items.*;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
/**
 *
 * @author Junseo Hwang 202322128
 */
@Component
public class ItemFactory {
    public static ConsumableItem createItem(ITEM_CODE itemCode) {
        return switch (itemCode) {
            case EMERGENCY_TREATMENT -> new ItemEmergencyTreatment();
            case SHIELD -> new ItemShield();
            case RAGE -> new ItemRage();
            case ESCAPE -> new ItemEscape();
            case REVERSAL -> new ItemReversal();
            case HEALING_POTION -> new ItemHealingPotion();
            case RESISTANCE -> new ItemResistance();
            case FINAL_BLOW -> new ItemFinalBlow();
            case PANACEA -> new ItemPanacea();
            default -> null;
        };
    }
}
