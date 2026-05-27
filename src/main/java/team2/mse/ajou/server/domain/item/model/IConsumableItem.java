package team2.mse.ajou.server.domain.item.model;

/**
 *
 * @author Junseo Hwang 202322128
 */
public interface IConsumableItem {
    /**
     * 이 함수를 호출했을 때 아이템이 사용가능하면 이 함수에서 아이템 로직을 실행합니다.
     */
    void useItemIfPossible();
    /**
     * 아이템이 사용 조건을 충족했는지 판단하는 함수
     * @return
     */
    boolean isAvailable();
}
