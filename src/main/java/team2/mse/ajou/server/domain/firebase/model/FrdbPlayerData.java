package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
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

    private HAND_CHOICE handChoice;

    private HAND_ELEMENTAL handElemental;

    private List<PERK> perkList;
    private List<PERK> perkChoiceList;

    private List<ITEM_CODE> receivedItemList;
    private List<ITEM_CODE> itemList;

    private int coin;
    private int elementalLevel;
    private int upgradeCost;

    public static FrdbPlayerData from(PlayerData playerData) {
        FrdbPlayerData data = new FrdbPlayerData();

        data.setUsername(playerData.getUsername());
        data.setReady(playerData.isReady());
        data.setWins(playerData.getWins());
        data.setHp(playerData.getHp());
        data.setAttacking(playerData.isAttacking());
        data.setSelecting(playerData.isSelecting());
        data.setFinalWinner(playerData.isFinalWinner());

        data.setHandChoice(playerData.getChoice());

        data.setHandElemental(playerData.getHandElemental());

        data.setPerkList(playerData.getPerkList());
        data.setPerkChoiceList(playerData.getPerkChoiceList());

        data.setReceivedItemList(playerData.getReceivedItemList());
        data.setItemList(playerData.getItemList());

        data.setCoin(playerData.getCoin());
        data.setElementalLevel(playerData.getElementalLevel());
        data.setUpgradeCost(playerData.getUpgradeCost());

        return data;
    }
}
