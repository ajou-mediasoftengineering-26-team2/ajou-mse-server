package team2.mse.ajou.server.domain.auth.model;

import java.util.UUID;

/**
 * (내부용. Service->Controller로 정보 전달용. Controller에서 이걸 바로 쓰면 큰일납니다!!!)
 */
public record LoginAndJoinResult(
        UUID playerId,
        UUID lobbyId
) {
}
