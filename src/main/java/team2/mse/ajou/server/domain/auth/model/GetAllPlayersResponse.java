package team2.mse.ajou.server.domain.auth.model;

import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * Response DTO for player list.
 * @param players List of all logged in players.
 *
 * @author Ahn Yubin / 202021088
 */
public record GetAllPlayersResponse(
        List<PlayerData> players
) {
}
