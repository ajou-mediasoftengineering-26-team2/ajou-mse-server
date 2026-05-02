package team2.mse.ajou.server.domain.auth.model;

import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

public record GetAllPlayersResponse(
        List<PlayerData> players
) {
}
