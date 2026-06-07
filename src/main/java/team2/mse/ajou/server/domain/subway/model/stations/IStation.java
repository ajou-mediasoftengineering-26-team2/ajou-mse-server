package team2.mse.ajou.server.domain.subway.model.stations;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * @author Junseo Hwang 202322128
 */
public interface IStation {
    void applyIfPossible(MatchData matchData);
    boolean isAvailable(MatchData matchData);
}
