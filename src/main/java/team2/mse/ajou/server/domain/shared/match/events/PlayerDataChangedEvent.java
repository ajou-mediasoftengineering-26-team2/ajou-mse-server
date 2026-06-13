package team2.mse.ajou.server.domain.shared.match.events;

import java.util.UUID;

/**
 * Event that fires whenever `PlayerData` was changed in the database.
 *
 * @param playerId Player ID.
 * @author Ahn Yubin / 202021088
 */
public record PlayerDataChangedEvent(
        UUID playerId
) {
}

