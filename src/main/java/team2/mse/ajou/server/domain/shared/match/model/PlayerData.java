package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.*;
import team2.mse.ajou.server.domain.shared.match.events.PlayerDataJpaListener;

import java.time.ZonedDateTime;
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
@EntityListeners({PlayerDataJpaListener.class})
@NoArgsConstructor
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
     * Hand elemental.
     */
    private HAND_ELEMENTAL handElemental = HAND_ELEMENTAL.NONE;

    //=========Perk==========
    /**
     * Perks.
     */
    private List<PERK> perkList = new ArrayList<>();
    /**
     * Perk list available for choosing (in the store screen).
     */
    private List<PERK> perkChoiceList = new ArrayList<>();
    /**
     * Selecting perk 'candidate'.
     */
    private PERK perkChoiceCurrent = null;

    //==========Item==========
    /**
     * Randomly received items for this round.
     */
    private List<ITEM_CODE> receivedItemList = new ArrayList<>();
    /**
     * Current items.
     */
    private List<ITEM_CODE> itemList = new ArrayList<>();
//    private List<ITEM_CODE> usedItemList = new ArrayList<>();

    /**
     * Money!
     */
    private int coin;

    /**
     * Level of Hand elemental.
     */
    private int elementalLevel;

    /**
     * Cost required for upgrading elemental.
     */
    private int upgradeCost;

    /**
     * List of activie status effect.
     */
    private List<STATUS_EFFECT> statusEffectList = new ArrayList<>();

    /**
     * Last updated time.
     */
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

    private int dodgeCount = 0; // 라운드 시작시에 0으로 초기화해야함

    public List<PERK> getPerkChoiceList() {
        if (perkChoiceList == null) {
            return perkChoiceList = new ArrayList<>();
        }
        return perkChoiceList;
    }

    public List<PERK> getPerkList() {
        if (perkList == null) {
            return perkList = new ArrayList<>();
        }
        return perkList;
    }

    public List<ITEM_CODE> getItemList() {
        if (itemList == null) {
            return itemList = new ArrayList<>();
        }
        return itemList;
    }

    public List<ITEM_CODE> getReceivedItemList() {
        if (receivedItemList == null) {
            return receivedItemList = new ArrayList<>();
        }
        return receivedItemList;
    }

    public void updateLastUpdated() {
        this.lastUpdated = ZonedDateTime.now();
    }

    /**
     * Deep copy.
     *
     * @param from
     */
    public PlayerData(PlayerData from) {
        this.id = from.id;
        this.joinedMatchId = from.joinedMatchId;
        this.username = from.username;
        this.ackState = from.ackState;
        this.wins = from.wins;
        this.hp = from.hp;
        this.isReady = from.isReady;
        this.isAttacking = from.isAttacking;
        this.isSelecting = from.isSelecting;
        this.isFinalWinner = from.isFinalWinner;
        this.choice = from.choice;
        this.handElemental = from.handElemental;
        this.perkList = new ArrayList<>(from.perkList);
        this.perkChoiceList = new ArrayList<>(from.perkChoiceList);
        this.perkChoiceCurrent = from.perkChoiceCurrent;
        this.receivedItemList = new ArrayList<>(from.receivedItemList);
        this.itemList = new ArrayList<>(from.itemList);
        this.coin = from.coin;
        this.elementalLevel = from.elementalLevel;
        this.upgradeCost = from.upgradeCost;
        this.statusEffectList = new ArrayList<>(from.statusEffectList);
    }
}
