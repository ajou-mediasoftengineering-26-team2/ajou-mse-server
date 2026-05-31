package team2.mse.ajou.server.domain.item.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
public interface IItemService {

    public void giveRandomItem(MatchData matchData);
    public void receiveItemAnimationEndAck(UUID id);
}
