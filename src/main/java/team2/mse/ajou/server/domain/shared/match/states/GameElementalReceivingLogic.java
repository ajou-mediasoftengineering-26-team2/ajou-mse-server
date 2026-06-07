package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;

/**
 * 플레이어가 elemental 받는 애니메이션 재생중
 *
 * @author Ahn Yubin / 202021088
 */
public class GameElementalReceivingLogic implements MatchStateLogic {
    @Override
    public void onMatchPlayerAckStateUpdate(RunningMatch context, List<ACK_TYPE> ackState) {
        var condition = ackState.size() >= 2 && ackState.stream().allMatch(ack -> ack == ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END);
        System.out.printf("\t[STATE] GameElementalReceivingLogic::onMatchPlayerAckStateUpdate(MATCH: %s) - %s\n", context.getMatchId(), ackState);

        // 다음 라운드 이동
        // (라운드 리셋 + state 설정)
        if (condition) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            System.out.printf("\t[STATE] GameElementalReceivingLogic::onMatchPlayerAckStateUpdate(MATCH: %s) | ALL ELEMENTAL_RECEIVE_ANIMATION_END ACK RECEIVED\n", context.getMatchId());

            context.updateMatchDataForRoundBegin(matchData);
            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);
        }
    }
}
