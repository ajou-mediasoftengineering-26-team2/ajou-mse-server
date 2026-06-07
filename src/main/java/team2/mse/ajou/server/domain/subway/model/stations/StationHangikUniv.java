package team2.mse.ajou.server.domain.subway.model.stations;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * Station - 홍대 입구
 * elemental의 레벨이 처음부터 최대레벨
 * @author Junseo Hwang 202322128
 */
public class StationHangikUniv implements IStation {
    private final int elementalMaxlevel = 5;
    @Override
    public void applyIfPossible(MatchData matchData) {
        if(isAvailable(matchData)) {
            List<PlayerData> players = matchData.getPlayers();
            for(PlayerData player : players) {
                player.setElementalLevel(elementalMaxlevel);
            }
        }
    }
    @Override
    public boolean isAvailable(MatchData matchData) {
        MATCH_STATE state = matchData.getState();
        return state == MATCH_STATE.GAME_ELEMENTAL_RECEIVING
                || state == MATCH_STATE.GAME_PERK_ITEM_RECEIVING;
    }
}
