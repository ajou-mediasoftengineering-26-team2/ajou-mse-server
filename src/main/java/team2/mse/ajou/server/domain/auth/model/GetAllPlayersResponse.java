package team2.mse.ajou.server.domain.auth.model;

import java.util.List;

public record GetAllPlayersResponse(
        List<PlayerInfo> players
) {
}
