package team2.mse.ajou.server.domain.shared.match.events;

import java.util.UUID;

/**
 * Event that fires whenever `MatchData` was changed in the database.
 *
 * @param matchId Match ID.
 * @author Ahn Yubin / 202021088
 */
public record MatchDataChangedEvent(
        UUID matchId
) {
}
