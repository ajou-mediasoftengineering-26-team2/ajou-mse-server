package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

/**
 * 게임 끝: 정상. 결과화면
 *
 * @author Ahn Yubin / 202021088
 */
public class EndResultLogic implements IMatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        IMatchStateLogic.super.onEnter(context);

        context.getMatchData(context.getMatchId()).ifPresentOrElse(matchData -> {
            PlayerData winningPlayer = null;
            try {
                winningPlayer = matchData.getPlayers().get(matchData.getWinnerPlayerIdx());

                System.out.printf(
                        "******************** GAME %s END! ********************\n\t* WINNER: %s (%s)\n",
                        context.getMatchId(),
                        winningPlayer.getUsername(),
                        winningPlayer.getId()
                );
            } catch (IndexOutOfBoundsException e) {
                /* NO-OP */
            }
        }, () -> {
            System.out.printf("******************** GAME %s END! ********************\n", context.getMatchId());
        });
    }
}
