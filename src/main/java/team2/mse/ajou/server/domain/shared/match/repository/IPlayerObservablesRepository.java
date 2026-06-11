package team2.mse.ajou.server.domain.shared.match.repository;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.observer.Observable;

import java.util.UUID;

/**
 * Repository that provides player related observable data.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IPlayerObservablesRepository {
    /**
     * Gets observable `PlayerData` that is updated whenever it's written to DB.
     *
     * @param playerId ID.
     * @return Observable.
     */
    Observable<PlayerData> getPlayerDataObservable(UUID playerId);

    /**
     * Gets observable `ACK_TYPE` for given player.
     *
     * @param playerId ID.
     * @return Observable, sent `ACK_TYPE`.
     */
    Observable<ACK_TYPE> getPlayerAckEventsObservable(UUID playerId);

    /**
     * Sends new `PlayerData` notice.
     *
     * @param playerData New `PlayerData`.
     */
    void sendPlayerDataUpdate(PlayerData playerData);

    /**
     * Sends new `ACK_TYPE` notice.
     *
     * @param playerId Player ID.
     * @param type     ACK.
     */
    void sendPlayerAckEvent(UUID playerId, ACK_TYPE type);
}
