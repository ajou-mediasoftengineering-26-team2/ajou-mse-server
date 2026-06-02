package team2.mse.ajou.server.domain.round.service;

import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
public interface IRoundService {
    public void receiveRoundStart(UUID playerId);
    public void receiveRoundEnd(UUID playerId);
}
