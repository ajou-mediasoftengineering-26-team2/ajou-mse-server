package team2.mse.ajou.server.domain.subway.model.stations;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * @author Junseo Hwang 202322128
 */
public class StationSillim implements IStation {
    @Override
    public void applyIfPossible(MatchData matchData) {
        
    }
    @Override
    public boolean isAvailable(MatchData matchData) {
        return false;
    }
}
