package team2.mse.ajou.server.domain.subway.model.stations;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * Station - 성수
 * 랜덤 아이템 지급 개수 2개로 증가
 * 이미 다른 곳에 구현되어 있어서 여기에 구현하지 않겠다
 * @author Junseo Hwang 202322128
 */
public class StationSeongSu implements IStation {
    private final int itemReceivingCount = 2;
    @Override
    public void applyIfPossible(MatchData matchData) {

    }
    @Override
    public boolean isAvailable(MatchData matchData) {
        return matchData.getState() == MATCH_STATE.GAME_PERK_CHOICE;
    }
}
