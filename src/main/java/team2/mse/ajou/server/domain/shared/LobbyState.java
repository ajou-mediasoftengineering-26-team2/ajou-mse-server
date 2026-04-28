package team2.mse.ajou.server.domain.shared;


public enum LobbyState {
    // 게임 시작 전
    WAITING,
    // 플레이어 빡종
    PLAYER_DISCONNECTED,
    PLAYER_DISCONNECTED_ALL,
    // 인게임
    ATK_CHOICE,
    DEF_CHOICE,
}
