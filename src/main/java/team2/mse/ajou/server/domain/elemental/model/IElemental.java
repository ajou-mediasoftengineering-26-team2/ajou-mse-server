package team2.mse.ajou.server.domain.elemental.model;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * 로직 계산을 위한 Elemental interface
 * Interface for logic calculation about elemental
 * If you want to implement a new elemental, implement this interface.
 * The implementation method is as follows:
 * 1. Write the condition under which the elemental effect is activated.
 * 2. Write logic like this:
 *          if(condition){
 *              the elemental effect logic
 *          }
 * Tip: If you extend the Elemental(abstract class) that implements this interface, you can use many utility functions.
 * @author Junseo Hwang 202322128
 */
public interface IElemental {
    /**
     * 이 함수를 호출하면 elemental 조건을 만족할 경우, 마지막 데미지 데이터에 elemental 효과가 적용됩니다.
     * When this function is called, if the elemental condition is satisfied,
     *      the elemental effect is applied to the last damage data.
     * If you implement the elemental effect logic, you should do it inside the elemental condition if statement.
     * @param matchData current match
     * @param ownerPlayerIdx index of elemental owner
     */
    void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx);

    /**
     * 이 함수를 호출하면 현재 elemental 조건이 충족되었는지 알 수 있습니다
     * When this function is called, you can check whether the current elemental condition is satisfied.
     * @param matchData current match
     * @param ownerPlayerIdx index of elemental onwer
     * @return if elemental condition is satisfied, return true
     */
    boolean isAvailable(MatchData matchData, int ownerPlayerIdx);
}