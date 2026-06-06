package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.UUID;

/**
 * 로비: 대기중 상태.
 * Lobby: Waiting for players state.
 *
 * @author Ahn Yubin / 202021088
 */
public class LobbyWaitingStateLogic implements MatchStateLogic {
    @Override
    public String getSerializedName() {
        return "LOBBY_WAITING";
    }

    @Override
    public boolean getIsIngame() {
        return false;
    }

    @Override
    public void onPlayerJoin(RunningMatch context, UUID playerId) {
        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerJoin(%s)\n", playerId);

        context.getPlayerData(playerId).ifPresent(playerData -> {
            System.out.printf("\t\t* Player name: %s\n", playerData.getUsername());

            var matchData = context.getMatchData(playerData.getJoinedMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            var players = matchData.getPlayers();

            if (players.size() >= 2) {
                System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerJoin | START GAME WITH PLAYERS: %s\n", playerId, players);
                matchData.setState(MATCH_STATE.LOBBY_START_COUNTDOWN);
            }
        });
    }

    @Override
    public void onPlayerLeave(RunningMatch context, UUID playerId) {
        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerLeave(%s)\n", playerId);

        context.getPlayerData(playerId).ifPresent(playerData -> {
            System.out.printf("\t\t* Player name: %s\n", playerData.getUsername());
        });
    }

    @Override
    public void onPlayerAck(RunningMatch context, UUID playerId, ACK_TYPE type) {
        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerAck(PLR: %s, TYPE: %s)\n", playerId, type);
    }

    @Override
    public void onEnter(RunningMatch context) {
        System.out.println("\t[STATE] LobbyWaitingStateLogic::onEnter()");
    }

    @Override
    public void onExit(RunningMatch context) {
        System.out.println("\t[STATE] LobbyWaitingStateLogic::onExit()");
    }
}

