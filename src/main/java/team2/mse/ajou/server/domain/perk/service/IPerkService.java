package team2.mse.ajou.server.domain.perk.service;

import team2.mse.ajou.server.domain.perk.model.PutPerkChoiceRequest;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.UUID;

public interface IPerkService {
    void putPerkChoice(UUID id, PERK perk);
    void putAck(UUID id);
}
