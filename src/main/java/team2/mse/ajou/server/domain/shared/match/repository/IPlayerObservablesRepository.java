package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.UUID;

public interface IPlayerObservablesRepository {
    Observable<PlayerData> getPlayerDataObservable(UUID playerId);

    Observable<ACK_TYPE> getPlayerAckEventsObservable(UUID playerId);

    void sendPlayerDataUpdate(PlayerData playerData);

    void sendPlayerAckEvent(UUID playerId, ACK_TYPE type);
}
