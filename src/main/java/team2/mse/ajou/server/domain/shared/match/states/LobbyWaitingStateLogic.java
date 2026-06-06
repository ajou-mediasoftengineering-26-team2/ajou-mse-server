package team2.mse.ajou.server.domain.shared.match.states;

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


}
