package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Item - 응급치료
 * HP 15 이하 시 즉시 HP +20 회복
 * heal +20 HP when HP is 15 or lower.
 * @author Junseo Hwang 202322128
 */
public class ItemEmergencyTreatment extends ConsumableItem {
    private final int triggerHp = 15;
    private final int healValue = 20;

    public ItemEmergencyTreatment() {
        super(ITEM_CODE.EMERGENCY_TREATMENT);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        heal(owner, healValue);
        consumeItem(owner, damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        return isInTurn(matchData)
                && owner != null
                && getCurrentDamageData(matchData) != null
                && hasItem(owner)
                && owner.getHp() <= triggerHp;
    }
}
