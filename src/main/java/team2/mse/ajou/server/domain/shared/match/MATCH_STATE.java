package team2.mse.ajou.server.domain.shared.match;


import java.util.List;

/**
 * 매치 상태.
 *
 * @author Ahn yubin / 202021088
 */
public enum MATCH_STATE {
    /** 대기 화면: 게임 시작 전 */
    LOBBY_WAITING,
    /** 대기 화면: 게임 시작 카운트다운 */
    LOBBY_START_COUNTDOWN,
    /** 인게임: 공격수 손 선택 */
    GAME_ATK_CHOICE,
    /** 인게임: 수비수 손 선택 */
    GAME_DEF_CHOICE,
    /** 인게임: 한 라운드 끝. 플레이어 사망 */
    GAME_ROUND_END_PLAYER_KO,
    /** 게임 끝: 정상. 결과화면 */
    END_RESULT,
    /** 게임 끝: 플레이어 빡종 */
    END_PLAYER_DISCONNECTED;

    public boolean isIngame() {
        return List.of(
                    MATCH_STATE.GAME_ATK_CHOICE,
                    MATCH_STATE.GAME_DEF_CHOICE,
                    MATCH_STATE.GAME_ROUND_END_PLAYER_KO
                )
                .contains(this);
    }
}
