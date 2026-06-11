package team2.mse.ajou.server.domain.item.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * Item Service
 * @author Junseo Hwang 202322128
 */
public interface IItemService {
    /**
     * This add Random Item at each player's receivedItemList
     * @param matchData match to received Item
     */
    public void giveRandomItem(MatchData matchData);

    /**
     * This save PERK_ITEM_RECEIVING_ANIMATION_END ACK
     * @param id uuid of player that has sent ACK
     */
    public void receiveItemAnimationEndAck(UUID id);
}
