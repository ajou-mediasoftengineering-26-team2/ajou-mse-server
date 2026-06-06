package team2.mse.ajou.server.domain.shared.match.states;

import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
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
public interface MatchStateLogic {
    // State별 플레이어 입력 등 콜백
    @Transactional
    default void onPlayerJoin(RunningMatch context, UUID playerId) {
        /* NO-OP */
    }

    @Transactional
    default void onPlayerLeave(RunningMatch context, UUID playerId) {
        /* NO-OP */
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
    default void onEnter(RunningMatch context) {
        System.out.printf("\t[STATE] %s::onEnter - %s\n", this.getClass().getSimpleName(), context.getMatchId());
    }

    @Transactional
    default void onExit(RunningMatch context) {
        System.out.printf("\t[STATE] %s::onExit - %s\n", this.getClass().getSimpleName(), context.getMatchId());
    }
}
