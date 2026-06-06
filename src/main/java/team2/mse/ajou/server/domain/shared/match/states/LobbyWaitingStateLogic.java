package team2.mse.ajou.server.domain.shared.match.states;

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

    }

    @Override
    public void onPlayerLeave(UUID playerId) {

    }

    @Override
    public void onPlayerAck() {

    }
}
