package team2.mse.ajou.server.domain.item.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;

/**
 * 모든 아이템은 ConsumableItem으로 관리됩니다.
 * @author Junseo Hwang 202322128
 */
@Data
public abstract class ConsumableItem implements IConsumableItem {
    protected final ITEM_CODE itemCode;

    public ConsumableItem(ITEM_CODE itemCode) {
        this.itemCode = itemCode;
    }


    /**
     * 아이템이 사용 조건을 충족했는지 판단하는 함수
     * @return
     */
    @Override
    public boolean isAvailable() {
        return  true;
    }
}
