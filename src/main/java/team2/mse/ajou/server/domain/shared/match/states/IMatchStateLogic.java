package team2.mse.ajou.server.domain.shared.match.states;

import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;
import java.util.UUID;

/**
 * 매치 State: State별 동작 정의 인터페이스.
 * Match State: Interface for logic to run for each states.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IMatchStateLogic {
    // State별 플레이어 입력 등 콜백
    @Transactional
    default void onPlayerJoin(RunningMatch context, UUID playerId) {
        System.out.printf("\t[STATE] %s::onPlayerJoin(%s) - %s\n", this.getClass().getSimpleName(), playerId, context.getMatchId());
    }

    @Transactional
    default void onPlayerLeave(RunningMatch context, UUID playerId) {
        System.out.printf("\t[STATE] %s::onPlayerLeave(%s) - %s\n", this.getClass().getSimpleName(), playerId, context.getMatchId());

        var matchData = context.getMatchData(context.getMatchId()).orElse(null);

        if (matchData == null) {
            System.out.printf("\t[STATE] %s::onPlayerLeave(%s) | MATCH DOES NOT EXIST IN DB! - %s\n", this.getClass().getSimpleName(), playerId, context.getMatchId());
            return;
        }

        var state = matchData.getState();

        if (state.isIngame()) {
            System.out.printf("\t[STATE] %s::onPlayerLeave(%s) | ALL PLAYERS LEFT, ENDING GAME! - %s\n", this.getClass().getSimpleName(), playerId, matchData.getId());
            matchData.setState(MATCH_STATE.END_PLAYER_DISCONNECTED);
            context.commitFrdbData(matchData);
            context.cancelTimer();
        } else {
            context.cancelTimer();
        }
    }

    @Transactional
    default void onPlayerAck(RunningMatch context, UUID playerId, ACK_TYPE type) {
        /* NO-OP */
    }

    @Transactional
    default void onMatchPlayerListUpdate(RunningMatch context, List<PlayerData> players) {
        /* NO-OP */
    }

    @Transactional
    default void onMatchPlayerAckStateUpdate(RunningMatch context, List<ACK_TYPE> ackState) {
        /* NO-OP */
    }

    @Transactional
    default void onMatchPlayerSelectingStateUpdate(RunningMatch context, List<Boolean> selectingState) {
        /* NO-OP */
    }

    @Transactional
    default void onEnter(RunningMatch context) {
        System.out.printf("\t[STATE] %s::onEnter - %s\n", this.getClass().getSimpleName(), context.getMatchId());
    }

    @Transactional
    default void onExit(RunningMatch context) {
        System.out.printf("\t[STATE] %s::onExit - %s\n", this.getClass().getSimpleName(), context.getMatchId());
    }
}
