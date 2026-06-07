package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 로비: 대기중 상태.
 * Lobby: Waiting for players state.
 *
 * @author Ahn Yubin / 202021088
 */
public class LobbyWaitingStateLogic implements MatchStateLogic {
    @Override
    public void onPlayerJoin(RunningMatch context, UUID playerId) {
        // System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerJoin(%s)\n", playerId);

        context.getPlayerData(playerId).ifPresent(playerData -> {
            System.out.printf("\t\t* Player name: %s\n", playerData.getUsername());
        });
    }

    @Override
    public void onPlayerLeave(RunningMatch context, UUID playerId) {
        // System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerLeave(%s)\n", playerId);

        context.getPlayerData(playerId).ifPresent(playerData -> {
            System.out.printf("\t\t* Player name: %s\n", playerData.getUsername());
        });
    }

    @Override
    public void onMatchPlayerListUpdate(RunningMatch context, List<PlayerData> players) {
        var playersFormatted = players.stream().map(player -> player.getId().toString()).collect(Collectors.joining(", ", "[", "]"));

        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onMatchPlayerListUpdate(%s)\n", playersFormatted);

        if (players.size() >= 2) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerJoin | START GAME WITH PLAYERS: %s\n", playersFormatted);
            matchData.setState(MATCH_STATE.LOBBY_START_COUNTDOWN);
            context.commitFrdbData(matchData);
        }
    }
}

