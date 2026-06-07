package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;

/**
 * 로비: 시작 대기 카운트다운 상태.
 *
 * @author Ahn Yubin / 202021088
 */
public class LobbyStartCountdownStateLogic implements MatchStateLogic {
    @Override
    public void onMatchPlayerAckStateUpdate(RunningMatch context, List<ACK_TYPE> ackState) {
        var condition = ackState.size() >= 2 && ackState.stream().allMatch(ack -> ack == ACK_TYPE.__TEST_ACK);
        System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onMatchPlayerAckStateUpdate(%s)\n", ackState);

        if (condition) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onMatchPlayerAckStateUpdate | ALL ACK RECEIVED\n");
            matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);
            context.commitFrdbData(matchData);
        }
    }
}
