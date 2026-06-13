package team2.mse.ajou.server.domain.item.service;

import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.item.model.IConsumableItem;
import team2.mse.ajou.server.domain.item.model.Items.*;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Item Factory
 * @author Junseo Hwang 202322128
 */
@Component
public class ItemFactory {
    /**
     * item enum에 맞는 item 로직 구현체를 생성한다.
     * This creates an implementation of the item logic that matches the item enum.
     * @param itemCode item enum
     * @return implementation of the item logic that matches the item enum
     */
    public static ConsumableItem createItem(ITEM_CODE itemCode) {
        if (itemCode == null) {
            return null;
        }

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

    /**
     * item enum list를 item 로직 구현체 list로 변환한다.
     * Converts an item enum list into a list of item logic implementations.
     * @param itemCodeList item enum list
     * @return list of implementation of the item logic that matches the list of item enum
     */
    public static List<IConsumableItem> createItemList(List<ITEM_CODE> itemCodeList) {
        List<IConsumableItem> itemList = new ArrayList<>();
        if (itemCodeList == null) {
            return itemList;
        }

        for (ITEM_CODE itemCode : itemCodeList) {
            IConsumableItem item = createItem(itemCode);
            if (item != null) {
                itemList.add(item);
            }
        }

        return itemList;
    }
}
