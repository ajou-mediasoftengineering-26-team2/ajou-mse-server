package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.DamageData;

public interface IPerk {

    void usePerkIfPossible(PlayerData player, DamageData damageData);
    /**
     * 아이템이 사용 조건을 충족했는지 판단하는 함수
     * @return
     */
    boolean isAvailable(PlayerData player, DamageData damageData);
}
