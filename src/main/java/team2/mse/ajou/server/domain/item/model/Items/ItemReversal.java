package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Item - 역전
 * HP 3 이하 시 즉시 상대 HP를 1로 설정
 * @author Junseo Hwang 202322128
 */
public class ItemReversal extends ConsumableItem {
    private final int triggerHp = 3;
    private final int setHpValue = 1;

    public ItemReversal() {
        super(ITEM_CODE.REVERSAL);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        opponent.setHp(setHpValue);
        consumeItem(owner, damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        return isInTurn(matchData)
                && owner != null
                && opponent != null
                && getCurrentDamageData(matchData) != null
                && hasItem(owner)
                && owner.getHp() <= triggerHp;
    }
}
