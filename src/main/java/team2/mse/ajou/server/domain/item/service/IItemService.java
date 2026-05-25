package team2.mse.ajou.server.domain.item.service;

import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
public interface IItemService {

    public void receiveItemAnimationEndAck(UUID id);
}
