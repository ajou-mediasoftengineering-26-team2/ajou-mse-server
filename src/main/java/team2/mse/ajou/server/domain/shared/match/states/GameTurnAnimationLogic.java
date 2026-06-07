package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;

/**
 * 인게임: 클라이언트가 공격/방어 애니메이션 재생 중
 *
 * @author Ahn Yubin / 202021088
 */
public class GameTurnAnimationLogic implements MatchStateLogic {
    @Override
    public void onMatchPlayerAckStateUpdate(RunningMatch context, List<ACK_TYPE> ackState) {
        var condition = ackState.size() >= 2 && ackState.stream().allMatch(ack -> ack == ACK_TYPE.TURN_ANIMATION_END);
        System.out.printf("\t[STATE] GameTurnAnimationLogic::onMatchPlayerAckStateUpdate(MATCH: %s) - %s\n", context.getMatchId(), ackState);

        if (condition) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            if (matchData.isKo()) {
                System.out.printf("\t[STATE] GameTurnAnimationLogic::onMatchPlayerAckStateUpdate(MATCH: %s) | ALL TURN_ANIMATION_END ACK RECEIVED (KO!!!)\n", context.getMatchId());

                // 일단 스테이트만 넘겨봐
                matchData.clearAck();
                matchData.setState(MATCH_STATE.GAME_ROUND_END_PLAYER_KO);
            } else {
                System.out.printf("\t[STATE] GameTurnAnimationLogic::onMatchPlayerAckStateUpdate(MATCH: %s) | ALL TURN_ANIMATION_END ACK RECEIVED (NEXT TURN)\n", context.getMatchId());

                // 턴 시작 시점으로 데이터 초기화
                context.updateMatchDataForTurnBegin(matchData);
            }

            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);
        }
    }
}
