package team2.mse.ajou.server.domain.perk.service;

import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;
import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
public interface IPerkService {
    void putPerkChoice(UUID id, PERK perk);

    void putAck(UUID id);

    List<PERK> getUnownedPerks(PlayerData playerData);
}
