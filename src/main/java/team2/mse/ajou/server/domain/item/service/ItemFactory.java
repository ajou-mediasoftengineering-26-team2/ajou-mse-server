package team2.mse.ajou.server.domain.item.service;

import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.item.model.Items.ItemEmergencyTreatment;
import team2.mse.ajou.server.domain.item.model.Items.ItemShield;
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
            //TODO: 아이템 다만들기
//            case RAGE -> new RageItem();
//            case ESCAPE -> new EscapeItem();
//            case REVERSAL -> new ReversalItem();
//            case POTION -> new PotionItem();
//            case RESISTANCE -> new ResistanceItem();
//            case FINAL_STRIKE -> new FinalStrikeItem();
//            case PANACEA -> new PanaceaItem();
            default -> null;
        };
    }
}
