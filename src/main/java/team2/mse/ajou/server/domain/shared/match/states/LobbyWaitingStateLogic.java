package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;

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
    public void onPlayerJoin(UUID playerId) {
        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerJoin(%s)\n", playerId);
    }

    @Override
    public void onPlayerLeave(UUID playerId) {
        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerLeave(%s)\n", playerId);
    }

    @Override
    public void onPlayerAck(UUID playerId, ACK_TYPE type) {
        System.out.printf("\t[STATE] LobbyWaitingStateLogic::onPlayerAck(PLR: %s, TYPE: %s)\n", playerId, type);
    }

    @Override
    public void onEnter() {
        System.out.println("\t[STATE] LobbyWaitingStateLogic::onEnter()");
    }

    @Override
    public void onExit() {
        System.out.println("\t[STATE] LobbyWaitingStateLogic::onExit()");
    }
}
