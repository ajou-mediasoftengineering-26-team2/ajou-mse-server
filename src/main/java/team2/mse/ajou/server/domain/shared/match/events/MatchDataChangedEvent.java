package team2.mse.ajou.server.domain.shared.match.events;

import java.util.UUID;

public record MatchDataChangedEvent(
        UUID matchId
) {
}
