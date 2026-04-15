package team2.mse.ajou.server.domain.auth.model;

public record PostPlayerResponse(
        String playerToken,
        String lobbyId,
        boolean isWaitNeeded
) {
}
