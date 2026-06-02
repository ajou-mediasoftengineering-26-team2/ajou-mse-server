package team2.mse.ajou.server.domain.shared.match;


import java.util.List;

/**
 * Match status.
 *
 * @author Junseo Hwang 202322128
 * @author Ahn yubin / 202021088
 */
public enum MATCH_STATE {
    // Lobby: before match
    /** 대기 화면: 게임 시작 전 */
    LOBBY_WAITING,
    // Lobby: game start countdown
    /** 대기 화면: 게임 시작 카운트다운 */
    LOBBY_START_COUNTDOWN,

    // match
    /**
     * 매치 시작
     * 이번역은 ~역 입니다. 등의 애니메이션 출력
     */
    MATCH_START,
    /**
     * 매치 종료
     * 결과 출력
     */
    MATCH_END_RESULT,
    /**
     * 매치 종료
     * 플레이어 빡종
     */
    MATCH_END_PLAYER_DISCONNECTED,

    // round
    /**
     * 라운드 시작
     * 동전 던지기 등의 애니메이션
     */
    GAME_ROUND_START_ANIMATION,

    // turn
    // Ingame: player move selection
    /** 인게임: 두 플레이어 손 선택 */
    GAME_PLAYER_CHOICE,
    // Ingame:
    /** 인게임: 두 플레이어 손 선택완료 후 결과 출력중 */
    GAME_CHOICE_FINISHED,
    // Ingame:
    /** 인게임: 클라이언트가 공격/방어 애니메이션 재생 중*/
    GAME_TURN_ANIMATION,
    // Ingame: end of a single round (caused by player KO)
    /** 인게임: 한 라운드 끝. 플레이어 사망 */
    GAME_ROUND_END_PLAYER_KO,

    // round
    /**
     * 플레이어가 hand elemental 선택중
     */
    GAME_ELEMENTAL_CHOICE,
    /**
     * 플레이어가 elemental 받는 애니메이션 재생중
     */
    GAME_ELEMENTAL_RECEIVING,

    /** 플레이어가 perk 선택 중*/
    GAME_PERK_CHOICE,
    /** 클라이언트가 perk, 아이템 받는 애니메이션 재생 중*/
    GAME_PERK_ITEM_RECEIVING,


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
                    MATCH_STATE.GAME_ROUND_START_ANIMATION,
                    MATCH_STATE.GAME_PLAYER_CHOICE,
                    MATCH_STATE.GAME_CHOICE_FINISHED,
                    MATCH_STATE.GAME_TURN_ANIMATION,
                    MATCH_STATE.GAME_ROUND_END_PLAYER_KO,

                    MATCH_STATE.GAME_ELEMENTAL_CHOICE,
                    MATCH_STATE.GAME_ELEMENTAL_RECEIVING,
                    MATCH_STATE.GAME_PERK_CHOICE,
                    MATCH_STATE.GAME_PERK_ITEM_RECEIVING
                )
                .contains(this);
    }

    /**
     * Determines whether this state is considered "receiving item".
     * @return Receiving items?
     */
    public boolean isReceivingItems() {
        return List.of(
                        MATCH_STATE.GAME_PERK_ITEM_RECEIVING,
                        MATCH_STATE.GAME_ELEMENTAL_RECEIVING
                )
                .contains(this);
    }
}
