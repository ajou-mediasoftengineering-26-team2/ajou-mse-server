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

    /**
     * Last updated time
     */
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

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

    public void updateLastUpdated() {
        this.lastUpdated = ZonedDateTime.now();
    }
}
