package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.UUID;

/**
 * 로비: 시작 대기 카운트다운 상태.
 *
 * @author Ahn Yubin / 202021088
 */
public class LobbyStartCountdownStateLogic implements MatchStateLogic {
    @Override
    public String getSerializedName() {
        return "LOBBY_START_COUNTDOWN";
    }

    @Override
    public boolean getIsIngame() {
        return false;
    }

    @Override
    public void onPlayerJoin(RunningMatch context, UUID playerId) {
        System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onPlayerJoin(%s)\n", playerId);
    }

    @Override
    public void onPlayerLeave(RunningMatch context, UUID playerId) {
        System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onPlayerLeave(%s)\n", playerId);
    }

    @Override
    public void onPlayerAck(RunningMatch context, UUID playerId, ACK_TYPE type) {
        System.out.printf("\t[STATE] LobbyStartCountdownStateLogic::onPlayerAck(PLR: %s, TYPE: %s)\n", playerId, type);
    }

    @Override
    public void onEnter(RunningMatch context) {
        System.out.println("\t[STATE] LobbyStartCountdownStateLogic::onEnter()");
    }

    @Override
    public void onExit(RunningMatch context) {
        System.out.println("\t[STATE] LobbyStartCountdownStateLogic::onExit()");
    }
}
