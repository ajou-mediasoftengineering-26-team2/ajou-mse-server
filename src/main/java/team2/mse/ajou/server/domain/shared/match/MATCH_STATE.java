package team2.mse.ajou.server.domain.shared.match;


import java.util.List;

/**
 * Match status.
 *
 * @author Ahn yubin / 202021088
 */
public enum MATCH_STATE {
    // Lobby: before match
    /** 대기 화면: 게임 시작 전 */
    LOBBY_WAITING,
    // Lobby: game start countdown
    /** 대기 화면: 게임 시작 카운트다운 */
    LOBBY_START_COUNTDOWN,
    // Ingame: attacker move selection
    /** 인게임: 공격수 손 선택 */
    GAME_ATK_CHOICE,
    // Ingame: defender move selection
    /** 인게임: 수비수 손 선택 */
    GAME_DEF_CHOICE,
    // Ingame: end of a single round (caused by player KO)
    /** 인게임: 한 라운드 끝. 플레이어 사망 */
    GAME_ROUND_END_PLAYER_KO,
    // Game over: show results
    /** 게임 끝: 정상. 결과화면 */
    END_RESULT,
    // Game over: player disconnected mid-match etc.
    /** 게임 끝: 플레이어 빡종 */
    END_PLAYER_DISCONNECTED;

    /**
     * Determines whether this state is considered "in-game".
     * @return In game?
     */
    public boolean isIngame() {
        return List.of(
                    MATCH_STATE.GAME_ATK_CHOICE,
                    MATCH_STATE.GAME_DEF_CHOICE,
                    MATCH_STATE.GAME_ROUND_END_PLAYER_KO
                )
                .contains(this);
    }
}
