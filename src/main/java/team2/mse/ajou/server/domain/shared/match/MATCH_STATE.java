package team2.mse.ajou.server.domain.shared.match;


import jakarta.persistence.Transient;
import team2.mse.ajou.server.domain.shared.match.states.*;
import team2.mse.ajou.server.domain.shared.states.GameChoiceFinishedLogic;

import java.util.List;

/**
 * Match status.
 *
 * @author Junseo Hwang 202322128
 * @author Ahn yubin / 202021088
 */
public enum MATCH_STATE {
    // Lobby: before match
    /**
     * 대기 화면: 게임 시작 전
     */
    LOBBY_WAITING(new LobbyWaitingStateLogic()),
    // Lobby: game start countdown
    /**
     * 대기 화면: 게임 시작 카운트다운
     */
    LOBBY_START_COUNTDOWN(new LobbyStartCountdownStateLogic()),

    // round
    /**
     * 라운드 시작
     * 동전 던지기 등의 애니메이션
     */
    GAME_ROUND_START_ANIMATION(new GameRoundStartAnimationLogic()),

    // turn
    // Ingame: player move selection
    /**
     * 인게임: 두 플레이어 손 선택
     */
    GAME_PLAYER_CHOICE(new GamePlayerChoiceLogic()),
    // Ingame:
    /**
     * 인게임: 두 플레이어 손 선택완료 후 결과 출력중
     */
    GAME_CHOICE_FINISHED(new GameChoiceFinishedLogic()),
    // Ingame:
    /**
     * 인게임: 클라이언트가 공격/방어 애니메이션 재생 중
     */
    GAME_TURN_ANIMATION(new LobbyWaitingStateLogic()),
    // Ingame: end of a single round (caused by player KO)
    /**
     * 인게임: 한 라운드 끝. 플레이어 사망
     */
    GAME_ROUND_END_PLAYER_KO(new LobbyWaitingStateLogic()),

    // round
    /**
     * 플레이어가 hand elemental 선택중
     */
    GAME_ELEMENTAL_CHOICE(new LobbyWaitingStateLogic()),
    /**
     * 플레이어가 elemental 받는 애니메이션 재생중
     */
    GAME_ELEMENTAL_RECEIVING(new LobbyWaitingStateLogic()),

    /**
     * 플레이어가 perk 선택 중
     */
    GAME_PERK_CHOICE(new LobbyWaitingStateLogic()),
    /**
     * 클라이언트가 perk, 아이템 받는 애니메이션 재생 중
     */
    GAME_PERK_ITEM_RECEIVING(new LobbyWaitingStateLogic()),


    // Game over: show results
    /**
     * 게임 끝: 정상. 결과화면
     */
    END_RESULT(new LobbyWaitingStateLogic()),
    // Game over: player disconnected mid-match etc.
    /**
     * 게임 끝: 플레이어 빡종
     */
    END_PLAYER_DISCONNECTED(new LobbyWaitingStateLogic());

    /**
     * Determines whether this state is considered "in-game".
     *
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
     *
     * @return Receiving items?
     */
    public boolean isReceivingItems() {
        return List.of(
                        MATCH_STATE.GAME_PERK_ITEM_RECEIVING,
                        MATCH_STATE.GAME_ELEMENTAL_RECEIVING
                )
                .contains(this);
    }

    @Transient
    private final MatchStateLogic logic;

    private MATCH_STATE(MatchStateLogic logic) {
        this.logic = logic;
    }

    public MatchStateLogic getLogic() {
        return logic;
    }
}
