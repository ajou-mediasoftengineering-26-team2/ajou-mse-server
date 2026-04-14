package team2.mse.ajou.server.domain.auth.model;

public record PostPlayerResponse(
        String authToken,
        String roomId
) {
}
