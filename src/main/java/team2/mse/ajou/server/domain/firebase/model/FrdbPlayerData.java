package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Player data used to set FRDB (Firebase Realtime DB). Internally converted from `PlayerData`.
 *
 * @author Ahn Yubin / 202021088
 */
@Data
public class FrdbPlayerData {
    private String username;
    private boolean isReady;
    private int wins;
    private int hp;
    private boolean isAttacking;
    private boolean isSelecting;
    private boolean isFinalWinner;

    public static FrdbPlayerData from(PlayerData playerData) {
        FrdbPlayerData data = new FrdbPlayerData();

        data.setUsername(playerData.getUsername());
        data.setReady(playerData.isReady());
        data.setWins(playerData.getWins());
        data.setHp(playerData.getHp());
        data.setAttacking(playerData.isAttacking());
        data.setSelecting(playerData.isSelecting());
        data.setFinalWinner(playerData.isFinalWinner());

        return data;
    }
}
