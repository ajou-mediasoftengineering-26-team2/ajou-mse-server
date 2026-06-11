package team2.mse.ajou.server.domain.subway.model.stations;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * 로직 계산을 위한 Subway Station interface
 * Interface for logic calculation about station
 * If you want to implement a new subway station, implement this interface.
 * The implementation method is as follows:
 * 1. Write the condition under which the station effect is activated.
 * 2. Write logic like this:
 *          if(condition){
 *              the station effect logic
 *          }
 * @author Junseo Hwang 202322128
 */
public interface IStation {
    /**
     * 이 함수를 호출하면 매치에 지하철 역 효과가 적용됩니다.
     * When this function is called, the subway station effect is applied to the match.
     * If you implement the station effect logic, you should do it inside the station condition if statement.
     * @param matchData current match
     */
    void applyIfPossible(MatchData matchData);

    /**
     * 이 함수를 호출하면 지하철 역 로직을 작동해야 하는 상황인지 판별할 수 있습니다.
     * When this function is called, you can determine whether the subway station logic should be executed.
     * @param matchData current match
     * @return if should execute logic, return true
     */
    boolean isAvailable(MatchData matchData);
}
