package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;

/**
 * 게임: 라운드 시작 애니메이션
 *
 * @author Ahn Yubin / 202021088
 */
public class GameRoundStartAnimationLogic implements MatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        MatchStateLogic.super.onEnter(context);

        context.getMatchData(context.getMatchId()).ifPresent(matchData -> {
            context.updateMatchDataForRoundBegin(matchData);
            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);
        });
    }

    @Override
    public void onMatchPlayerAckStateUpdate(RunningMatch context, List<ACK_TYPE> ackState) {
        var condition = ackState.size() >= 2 && ackState.stream().allMatch(ack -> ack == ACK_TYPE.ROUND_START_ANIMATION_END);
        System.out.printf("\t[STATE] GameRoundStartAnimationLogic::onMatchPlayerAckStateUpdate(%s)\n", ackState);

        if (condition) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            System.out.printf("\t[STATE] GameRoundStartAnimationLogic::onMatchPlayerAckStateUpdate | ALL ACK RECEIVED\n");

            // 턴 시작 시점으로 데이터 초기화
            context.updateMatchDataForTurnBegin(matchData);
            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);

            // (시간 제한은 `GamePlayerChoiceLogic` 상태 진입에서 설정합니다.)
        }
    }
}
