package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * 로직 계산을 위한 Perk interface
 * Interface for logic calculation about perk
 * If you want to implement a new perk, implement this interface.
 * The implementation method is as follows:
 * 1. Write the condition under which the perk effect is activated.
 * 2. Write logic like this:
 *          if(condition){
 *              the perk effect logic
 *          }
 * Tip: If you extend the Perk(abstract class) that implements this interface, you can use many utility functions.
 * @author Junseo Hwang 202322128
 */
public interface IPerk {
    /**
     * 이 함수를 호출하면 perk 조건을 만족할 경우, 마지막 데미지 데이터에 perk 효과가 적용됩니다.
     * When this function is called, if the perk condition is satisfied,
     *      the perk effect is applied to the last damage data.
     * If you implement the perk effect logic, you should do it inside the perk condition if statement.
     * @param matchData current match
     * @param ownerPlayerIdx index of perk owner
     */
    void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx);
    /**
     * 이 함수를 호출하면 현재 perk 조건이 충족되었는지 알 수 있습니다
     * When this function is called, you can check whether the current perk condition is satisfied.
     * @param matchData current match
     * @param ownerPlayerIdx index of perk onwer
     * @return if perk condition is satisfied, return true
     */
    boolean isAvailable(MatchData matchData, int ownerPlayerIdx);
}
