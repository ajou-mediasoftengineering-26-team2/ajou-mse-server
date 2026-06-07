package team2.mse.ajou.server.domain.subway.model.stations;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * Station - 강남
 * 최대 체력이 -5 낮음
 * @author Junseo Hwang 202322128
 */
public class StationGangNam implements IStation {
    private final int reducedMaxHpValue = 5;
    @Override
    public void applyIfPossible(MatchData matchData) {
        if(isAvailable(matchData)) {
            List<PlayerData> players = matchData.getPlayers();
            for(PlayerData player : players) {
                player.setMaxHp(player.getMaxHp() - reducedMaxHpValue);
            }
        }
    }
    @Override
    public boolean isAvailable(MatchData matchData) {
        MATCH_STATE state = matchData.getState();
        return state == MATCH_STATE.LOBBY_START_COUNTDOWN
                || state == MATCH_STATE.GAME_ELEMENTAL_RECEIVING
                || state == MATCH_STATE.GAME_PERK_ITEM_RECEIVING;
    }
}
