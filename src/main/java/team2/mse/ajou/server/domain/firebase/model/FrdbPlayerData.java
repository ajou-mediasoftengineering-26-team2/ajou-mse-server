package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Firebase RDB에 저장할때 사용되는 플레이어 정보. 실제로는 PlayerData 클래스를 사용하니 해당 클래스를 참고바랍니다.
 *
 * @author Ahn yubin / 202021088
 */
@Data
public class FrdbPlayerData {
    private String username;
    private boolean isReady;
    private int wins;
    private int hp;
    private boolean isAttacking;
    private boolean isSelecting;

    public static FrdbPlayerData from(PlayerData playerData) {
        FrdbPlayerData data = new FrdbPlayerData();

        data.setUsername(playerData.getUsername());
        data.setReady(playerData.isReady());
        data.setWins(playerData.getWins());
        data.setHp(playerData.getHp());
        data.setAttacking(playerData.isAttacking());
        data.setSelecting(playerData.isSelecting());

        return data;
    }
}
