package team2.mse.ajou.server.domain.item.model;

import lombok.Getter;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * IConsumalbeItem의 구현체
 * Item 로직 계산을 위한 유틸 함수들을 제공합니다.
 * Class for item logic calculation
 * This provides utility functions for Item logic calculation.
 * @author Junseo Hwang 202322128
 */
@Getter
public abstract class ConsumableItem implements IConsumableItem {
    protected final ITEM_CODE itemCode;

    public ConsumableItem(ITEM_CODE itemCode) {
        this.itemCode = itemCode;
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        return false;
    }

    // 아래는 유틸함수들

    protected boolean hasTwoPlayers(MatchData matchData) {
        return matchData != null && matchData.getPlayers() != null && matchData.getPlayers().size() >= 2;
    }

    protected boolean isValidOwnerIdx(MatchData matchData, int ownerPlayerIdx) {
        return hasTwoPlayers(matchData)
                && ownerPlayerIdx >= 0
                && ownerPlayerIdx < matchData.getPlayers().size();
    }

    protected PlayerData getOwner(MatchData matchData, int ownerPlayerIdx) {
        if (!isValidOwnerIdx(matchData, ownerPlayerIdx)) {
            return null;
        }
        return matchData.getPlayers().get(ownerPlayerIdx);
    }

    protected PlayerData getOpponent(MatchData matchData, int ownerPlayerIdx) {
        if (!isValidOwnerIdx(matchData, ownerPlayerIdx)) {
            return null;
        }
        return matchData.getPlayers().get(ownerPlayerIdx ^ 1);
    }

    protected int getDefenderPlayerIdx(MatchData matchData) {
        if (!hasTwoPlayers(matchData)) {
            return -1;
        }
        return matchData.getAttackerPlayerIdx() ^ 1;
    }

    protected boolean isOwnerAttacker(MatchData matchData, int ownerPlayerIdx) {
        return matchData != null && matchData.getAttackerPlayerIdx() == ownerPlayerIdx;
    }

    protected boolean isOwnerDefender(MatchData matchData, int ownerPlayerIdx) {
        return getDefenderPlayerIdx(matchData) == ownerPlayerIdx;
    }

    protected boolean isAttackSuccess(MatchData matchData) {
        return matchData != null && matchData.isAttackSuccess();
    }

    protected boolean isInTurn(MatchData matchData) {
        return matchData != null && matchData.getState() == MATCH_STATE.GAME_CHOICE_FINISHED;
    }

    protected DamageData getCurrentDamageData(MatchData matchData) {
        if (matchData == null) {
            return null;
        }

        List<DamageData> damageDataList = matchData.getDamageDataList();
        if (damageDataList == null || damageDataList.isEmpty()) {
            return null;
        }
        return damageDataList.getLast();
    }

    protected boolean isFirstDamage(DamageData damageData) {
        return damageData != null && damageData.getDamageIndex() == 0;
    }

    /**
     * 혹시 아이템이 중간에 사용되어 순회도중 사라지는 상황을 방지함
     * @param owner
     * @return
     */
    protected boolean hasItem(PlayerData owner) {
        return owner != null && owner.getItemList() != null && owner.getItemList().contains(itemCode);
    }

    /**
     * 아이템을 사용하는 함수
     * 이 함수를 호출하면 자동으로 damageData에 사용된 아이템으로 등록되고,
     * 플레이어의 아이템 리스트에서 제외됨.
     * @param owner
     * @param damageData
     */
    protected void consumeItem(PlayerData owner, DamageData damageData) {
        if (owner == null || owner.getItemList() == null) {
            return;
        }

        // 플레이어 리스트와 현재 순회하는 리스트는 별개의 리스트라서 remove해도 됨
        owner.getItemList().remove(itemCode);
        if(damageData != null){
            damageData.addUsedItem(itemCode);
        }
    }

    /**
     * 아이템으로 플레이어 hp 회복
     * @param player
     * @param healValue
     */
    protected void heal(PlayerData player, int healValue) {
        if(player == null){
            return;
        }

        player.setHp(Math.min(player.getMaxHp(),player.getHp() + healValue));
    }
}
