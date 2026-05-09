package team2.mse.ajou.server.domain.auth.model;

import java.util.UUID;

/**
 * (INTERNAL USE. For passing data between Service & Controller. DON'T USE THIS DIRECTLY IN CONTROLLERS!!!)
 *
 * @author Ahn Yubin / 202021088
 */
public record LoginAndJoinResult(
        UUID playerId,
        UUID lobbyId
) {
}
