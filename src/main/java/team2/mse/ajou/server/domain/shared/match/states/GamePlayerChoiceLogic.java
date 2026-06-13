package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

/**
 * 인게임: 두 플레이어 손 선택
 * Ingame: player action selection.
 *
 * @author Ahn Yubin / 202021088
 */
public class GamePlayerChoiceLogic implements IMatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        IMatchStateLogic.super.onEnter(context);

        // 플레이어 입력 (5초 제한)
        // Wait for player choice (duration of 5s)
        context.setTimerAndRun(5, () -> {
            context.getMatchData(context.getMatchId()).ifPresent(newMatchData -> {
                System.out.printf("\t[STATE] GamePlayerChoiceLogic::setTimerAndRun(MATCH: %s) | PLAYER INPUT TIMER END!\n", newMatchData.getId());

                // 여기서 호출되는 `calculateTurn()` 은 `GAME_CHOICE_FINISHED` 로 상태 전이해주는듯..?
                // `calculateTurn()` here switches the state to `GAME_CHOICE_FINISHED`.
                context.updateMatchDataForCalculateTurn(newMatchData);
                context.commitMatchData(newMatchData);
                context.commitFrdbData(newMatchData);
            });
        });
    }
}
