package team2.mse.ajou.server.domain.item.model.Items;

import team2.mse.ajou.server.domain.item.model.ConsumableItem;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;

/**
 * Item - 만병통치
 * 상태이상 있을 시 모든 상태이상 해제
 * clears all status effects when player has status effect
 * @author Junseo Hwang 202322128
 */
public class ItemPanacea extends ConsumableItem {

    public ItemPanacea() {
        super(ITEM_CODE.PANACEA);
    }

    @Override
    public void useItemIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        owner.getStatusEffectList().clear();
        consumeItem(owner, damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        return isInTurn(matchData)
                && owner != null
                && getCurrentDamageData(matchData) != null
                && hasItem(owner)
                && owner.getStatusEffectList() != null
                && owner.getStatusEffectList().stream().anyMatch(effect -> effect != STATUS_EFFECT.NONE);
    }
}
