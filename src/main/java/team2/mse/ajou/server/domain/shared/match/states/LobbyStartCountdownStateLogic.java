package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;
import java.util.UUID;

/**
 * 로비: 시작 대기 카운트다운 상태.
 *
 * @author Ahn Yubin / 202021088
 */
public class LobbyStartCountdownStateLogic implements MatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        MatchStateLogic.super.onEnter(context);

        context.setTimerAndRun(5, () -> {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::setTimerAndRun | TIMER FIRED! - %s\n", matchData.getId());
            matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);

            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);
        });
    }

    @Override
    public void onPlayerLeave(RunningMatch context, UUID playerId) {
        var matchData = context.getMatchData(context.getMatchId()).orElse(null);

        if (matchData == null) {
            System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onPlayerLeave(%s) | MATCH DOES NOT EXIST IN DB! - %s\n", playerId, context.getMatchId());
            return;
        }

        var players = matchData.getPlayers();

        if (!players.isEmpty()) {
            System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onPlayerLeave(%s) | PLAYER LEFT, CANCELLING TIMER AND REVERTING TO WAITING! - %s\n", playerId, matchData.getId());
            matchData.setState(MATCH_STATE.LOBBY_WAITING);
            context.commitFrdbData(matchData);
            context.cancelTimer();
        } else {
            System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onPlayerLeave(%s) | ALL PLAYERS LEFT, ENDING GAME! - %s\n", playerId, matchData.getId());
            matchData.setState(MATCH_STATE.END_PLAYER_DISCONNECTED);
            context.commitFrdbData(matchData);
            context.cancelTimer();
        }
    }
}
