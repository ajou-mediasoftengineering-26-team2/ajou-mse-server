package team2.mse.ajou.server.domain.item.model;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * 로직 계산을 위한 Item interface
 * Interface for logic calculation about item
 * If you want to implement a new item, implement this interface.
 * The implementation method is as follows:
 * 1. Write the condition under which the item effect is activated.
 * 2. Write logic like this:
 *          if(condition){
 *              the Item effect logic
 *          }
 * Tip: If you extend the Item(abstract class) that implements this interface, you can use many utility functions.
 * @author Junseo Hwang 202322128
 */
public interface IConsumableItem {
    /**
     * 이 함수를 호출하면 item 조건을 만족할 경우, 마지막 데미지 데이터에 item 효과가 적용됩니다.
     * When this function is called, if the item condition is satisfied,
     *      the item effect is applied to the last damage data.
     * If you implement the item effect logic, you should do it inside the item condition if statement.
     * @param matchData current match
     * @param ownerPlayerIdx index of item owner
     */
    void useItemIfPossible(MatchData matchData, int ownerPlayerIdx);
    /**
     * 이 함수를 호출하면 현재 item 조건이 충족되었는지 알 수 있습니다
     * When this function is called, you can check whether the current item condition is satisfied.
     * @param matchData current match
     * @param ownerPlayerIdx index of item onwer
     * @return if item condition is satisfied, return true
     */
    boolean isAvailable(MatchData matchData, int ownerPlayerIdx);
}
