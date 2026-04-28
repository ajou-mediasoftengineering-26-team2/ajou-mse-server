package team2.mse.ajou.server.domain.firebase.lobby;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.PlayerData;

/**
 * Firebase RDB에 저장할때 사용되는 플레이어 정보.
 *
 * @author yubin
 */
@AllArgsConstructor
@Getter
@Setter
public class FrdbPlayerData {
    private String username;
    private boolean isReady;
    // 인게임
    private int hp;

    public static FrdbPlayerData from(PlayerData playerData) {
        return new FrdbPlayerData(
                playerData.getUsername(),
                playerData.isReady(),
                playerData.getHp()
        );
    }
}
