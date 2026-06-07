package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

/**
 * 인게임: 두 플레이어 손 선택
 *
 * @author Ahn Yubin / 202021088
 */
public class GamePlayerChoiceLogic implements MatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        MatchStateLogic.super.onEnter(context);

        // 플레이어 입력 (5초 제한)
        context.setTimerAndRun(5, () -> {
            context.getMatchData(context.getMatchId()).ifPresent(newMatchData -> {
                System.out.printf("\t[STATE] GamePlayerChoiceLogic::setTimerAndRun | PLAYER INPUT TIMER END! - %s\n", newMatchData.getId());

                context.updateMatchDataForCalculateTurn(newMatchData);
                context.commitMatchData(newMatchData);
                context.commitFrdbData(newMatchData);
            });
        });
    }
}
