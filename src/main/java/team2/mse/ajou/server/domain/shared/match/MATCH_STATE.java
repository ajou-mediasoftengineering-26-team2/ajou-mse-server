package team2.mse.ajou.server.domain.shared.match;


import jakarta.persistence.Transient;
import team2.mse.ajou.server.domain.shared.match.states.*;

import java.util.List;

/**
 * Match status.
 *
 * @author Junseo Hwang 202322128
 * @author Ahn yubin / 202021088
 */
public enum MATCH_STATE {
    // INTRO
    /**
     * Lobby: before match.
     */
    LOBBY_WAITING(new LobbyWaitingStateLogic()),
    /**
     * Lobby: game start countdown.
     */
    LOBBY_START_COUNTDOWN(new LobbyStartCountdownStateLogic()),
    /**
     * Ingame: Round begin intro animation.
     */
    GAME_ROUND_START_ANIMATION(new GameRoundStartAnimationLogic()),

    // TURN
    /**
     * Ingame: player action selection.
     */
    GAME_PLAYER_CHOICE(new GamePlayerChoiceLogic()),
    /**
     * Ingame: send results after each turn timer.
     */
    GAME_CHOICE_FINISHED(new GameChoiceFinishedLogic()),
    /**
     * Ingame: play results animation (i.e. attack/defence animation).
     */
    GAME_TURN_ANIMATION(new GameTurnAnimationLogic()),
    /**
     * Ingame: end of a single round (caused by player KO)
     */
    GAME_ROUND_END_PLAYER_KO(new GameRoundEndPlayerKoLogic()),

    // SHOP
    /**
     * Shop: hand elemental.
     */
    GAME_ELEMENTAL_CHOICE(new GameElementalChoiceLogic()),
    /**
     * Shop: play hand elemental receiving animation.
     */
    GAME_ELEMENTAL_RECEIVING(new GameElementalReceivingLogic()),

    /**
     * Shop: perk.
     */
    GAME_PERK_CHOICE(new GamePerkChoiceLogic()),
    /**
     * Shop: play hand perk receiving animation.
     */
    GAME_PERK_ITEM_RECEIVING(new GamePerkItemReceivingLogic()),


    // GAME OVER
    /**
     * GAME OVER: Normal best of five player win.
     */
    END_RESULT(new EndResultLogic()),
    /**
     * GAME OVER: player disconnected mid-match etc.
     */
    // TODO: Maybe add auto-deletion to matches that are no longer used?
    END_PLAYER_DISCONNECTED(new IMatchStateLogic() {

    });

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

    /**
     * Match state logic implementation that implements appropriate callbacks.
     */
    @Transient
    private final IMatchStateLogic logic;

    private MATCH_STATE(IMatchStateLogic logic) {
        this.logic = logic;
    }

    public IMatchStateLogic getLogic() {
        return logic;
    }
}
