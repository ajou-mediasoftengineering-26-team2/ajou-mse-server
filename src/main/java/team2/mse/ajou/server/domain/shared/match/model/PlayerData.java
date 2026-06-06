package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Data;
import team2.mse.ajou.server.domain.item.service.ItemFactory;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Player data. Entity saved to internal DB.
 *
 * @author Ahn Yubin / 202021088
 * @author Junseo Hwang / 202322128
 */
@Entity
@Data
public class PlayerData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Currently joining / playing match ID.
     */
    private UUID joinedMatchId;
    /**
     * Player username.
     */
    @Column(unique = true)
    private String username;
    /**
     * player's ack state.
     * if player does not spend ack, ack state is NO_ACK
     */
    private ACK_TYPE ackState = ACK_TYPE.NO_ACK;
    /**
     * Points. (kills/wins)
     */
    private int wins = 0;
    /**
     * maxHp
     */
    private int maxHp = 10;
    /**
     * Health.
     */
    private int hp = 10;
    /**
     * (Lobby) Whether this player ready for start of the match.
     */
    private boolean isReady = false;

    //==========Turn==============
    /**
     * (Turn) Whether this player has the 'attacker' role.
     */
    private boolean isAttacking = false;
    /**
     * (Turn) Whether this player must select their moves. (in time)
     */
    private boolean isSelecting = false;
    /**
     * (End of the round) Whether this player has won this match.
     */
    private boolean isFinalWinner = false;
    /**
     * Selected move.
     */
    private HAND_CHOICE choice;

    //=========Elemental======
    /**
     * 적용중인 hand elemental
     */
    private HAND_ELEMENTAL handElemental = HAND_ELEMENTAL.NONE;

    //=========Perk==========
    /**
     * 적용중인 Perks
     */
    private List<PERK> perkList = new ArrayList<>();
    /**
     * 클라이언트 화면에 띄워진 perk list
     */
    private List<PERK> perkChoiceList = new ArrayList<>();
    /**
     * 클라이언트가 현재 선택중인 perk (이후 `MATCH_STATE.GAME_PERK_ITEM_RECEIVING` 상태로 넘어가는 시점에서 실제로 `perkList` 적용)
     */
    private PERK perkChoiceCurrent = null;

    //==========Item==========
    /**
     * 랜덤으로 받은 아이템
     */
    private List<ITEM_CODE> receivedItemList = new ArrayList<>();
    /**
     * 보유한 아이템
     */
    private List<ITEM_CODE> itemList = new ArrayList<>();
//    private List<ITEM_CODE> usedItemList = new ArrayList<>();

    private int coin;

    private int elementalLevel;
    private int upgradeCost;

    private List<STATUS_EFFECT> statusEffectList = new ArrayList<>();

    private int dodgeCount = 0; // 라운드 시작시에 0으로 초기화해야함

    public List<PERK> getPerkChoiceList() {
        if(perkChoiceList == null) {
            return perkChoiceList = new ArrayList<>();
        }
        return perkChoiceList;
    }

    public List<PERK> getPerkList() {
        if(perkList == null) {
            return perkList = new ArrayList<>();
        }
        return perkList;
    }

    public List<ITEM_CODE> getItemList() {
        if(itemList == null) {
            return itemList = new ArrayList<>();
        }
        return itemList;
    }

    public List<ITEM_CODE> getReceivedItemList() {
        if(receivedItemList == null) {
            return receivedItemList = new ArrayList<>();
        }
        return receivedItemList;
    }
}
