package team2.mse.ajou.server.domain.shared.states;

import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;
import team2.mse.ajou.server.domain.shared.match.states.MatchStateLogic;

import java.util.List;

/**
 * 인게임: 두 플레이어 손 선택완료 후 결과 출력중
 *
 * @author Ahn Yubin / 202021088
 */
public class GameChoiceFinishedLogic implements MatchStateLogic {
    @Override
    public void onMatchPlayerSelectingStateUpdate(RunningMatch context, List<Boolean> selectingState) {
        var condition = (selectingState.size() >= 2) && selectingState.stream().noneMatch(value -> value);
        System.out.printf("\t[STATE] GameChoiceFinishedLogic::onMatchPlayerSelectingStateUpdate(MATCH: %s) - %s, %b\n", context.getMatchId(), selectingState, condition);

        if (condition) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            System.out.printf("\t[STATE] GameChoiceFinishedLogic::onMatchPlayerSelectingStateUpdate(MATCH: %s) | ALL CHOICE SENT, CONTINUE!\n", context.getMatchId());

            // 여기서 호출되는 `calculateTurn()` 은 진짜로 값 계산하는 것
            context.updateMatchDataForCalculateTurn(matchData);
            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);
        }
    }
}
