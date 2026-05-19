package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.ITEM;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

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

    private List<PERK> perkList;
    private List<PERK> perkChoiceList;

    private ITEM receivedItem;
    private List<ITEM> itemList;

    public static FrdbPlayerData from(PlayerData playerData) {
        FrdbPlayerData data = new FrdbPlayerData();

        data.setUsername(playerData.getUsername());
        data.setReady(playerData.isReady());
        data.setWins(playerData.getWins());
        data.setHp(playerData.getHp());
        data.setAttacking(playerData.isAttacking());
        data.setSelecting(playerData.isSelecting());
        data.setFinalWinner(playerData.isFinalWinner());

        data.setPerkList(playerData.getPerkList());
        data.setPerkChoiceList(playerData.getPerkChoiceList());

        data.setReceivedItem(playerData.getReceviedItem());
        data.setItemList(playerData.getItemList());

        return data;
    }
}
