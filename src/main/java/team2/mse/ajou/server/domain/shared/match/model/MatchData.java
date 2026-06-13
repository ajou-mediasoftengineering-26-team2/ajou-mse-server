package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.events.MatchDataJpaListener;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Match data. Entity saved to internal DB.
 *
 * @author Ahn Yubin / 202021088
 */
@Entity
@EntityListeners({MatchDataJpaListener.class})
@NoArgsConstructor
@Getter
@Setter
public class MatchData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * Subway station ID at the time of the start of the match.
     * TODO: String -> Enum migration
     */
    private String station = "<UNKNOWN>";
    /**
     * Countdown timer. (start timestamp)
     */
    private ZonedDateTime countdownStartTime = ZonedDateTime.now();
    /**
     * Countdown timer. (seconds from `countDownStartTime`)
     * Therefore, end time is calculated as `countDownStartTime + countdownSec`.
     */
    private int countdownSec = 0;
    /**
     * Match state. (e.g. Player waiting, Attack/defence selection, Player KO)
     */
    private MATCH_STATE state = MATCH_STATE.LOBBY_WAITING;
    /**
     * Winning player index.
     */
    private int winnerPlayerIdx = -1;
    /**
     * Current turn. (i.e. Number of attack -> defence selections)
     */
    private int currentTurn = 0;
    /**
     * Current round. (i.e. Number of players KO'd in total)
     */
    private int currentRound = 0;
    /**
     * Current player index. (i.e. player who are selecting moves)
     */
    private int currentPlayerIdx = 0;
    /**
     * Attacking player index.
     */
    private int attackerPlayerIdx = 0;
    /**
     * Whether the last attacking players attack have landed.
     */
    private boolean isAttackSuccess = false;
    /**
     * List of players joined in this match.
     */
    private boolean ko = false;

    /**
     * Forbidden behaviour for `CITY_HALL` station.
     */
    private HAND_CHOICE forbiddenBehavior;

    /**
     * List of joined players.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PlayerData> players = new ArrayList<>();

    /**
     * List of damage dealt for this turn.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("damageIndex ASC")
    private List<DamageData> damageDataList = new ArrayList<>();

    /**
     * List of damage defended.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DefendData defendData = new DefendData();

    /**
     * Last updated time.
     */
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

    /**
     * Find player by UUID.
     *
     * @param id UUID.
     * @return Player data. null if not found.
     */
    @Transient
    public PlayerData findPlayerById(UUID id) {
        return players.stream()
                .filter(playerData -> playerData.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find player INDEX by UUID.
     *
     * @param id UUID.
     * @return Player index. -1 if not found.
     */
    @Transient
    public int findPlayerIndexById(UUID id) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId().equals(id)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Adds or modifies player data to this match.
     *
     * @param player Player data to add/modify.
     * @return Index of added/modified player.
     */
    @Transient
    public int updatePlayer(PlayerData player) {
        int idx = findPlayerIndexById(player.getId());
        if (idx != -1) {
            players.set(idx, player);
            return idx;
        } else {
            players.add(player);
            return players.size() - 1;
        }
    }

    /**
     * Remove player by UUID.
     *
     * @param id Player UUID.
     * @return Whether player was successfully removed.
     */
    @Transient
    public boolean removePlayer(UUID id) {
        int idx = findPlayerIndexById(id);
        if (idx != -1) {
            players.remove(idx);
            return true;
        }

        return false;
    }

    /**
     * add damage data to the end of damage list of match
     * @param damageData damage date to be added to the damage list
     * @author Junseo Hwang 202322128
     */
    @Transient
    public void addDamageData(DamageData damageData) {
        damageDataList.add(damageData);
    }

    /**
     * clear damage list of match
     * @author Junseo Hwang 202322128
     */
    @Transient
    public void clearDamageDataList() {
        // need null check
        if (damageDataList == null) {
            damageDataList = new ArrayList<>();
        }
        damageDataList.clear();
    }

    @Transient
    public void updateLastUpdated() {
        lastUpdated = ZonedDateTime.now();
    }

    @Transient
    public void clearAck() {
        for (var player: players) {
            player.setAckState(ACK_TYPE.NO_ACK);
        }
    }

    public DefendData getDefendData() {
        if(defendData == null) {
            this.defendData = new DefendData();
        }
        return defendData;
    }

    /**
     * Deep copy.
     *
     * @param from
     */
    public MatchData(MatchData from) {
        this.id = from.id;
        this.station = from.station;
        this.countdownStartTime = from.countdownStartTime;
        this.countdownSec = from.countdownSec;
        this.state = from.state;
        this.winnerPlayerIdx = from.winnerPlayerIdx;
        this.currentTurn = from.currentTurn;
        this.currentRound = from.currentRound;
        this.currentPlayerIdx = from.currentPlayerIdx;
        this.attackerPlayerIdx = from.attackerPlayerIdx;
        this.isAttackSuccess = from.isAttackSuccess;
        this.ko = from.ko;
        this.forbiddenBehavior = from.forbiddenBehavior;
        this.players = new ArrayList<>(from.players.stream().map(PlayerData::new).toList());
        this.damageDataList = new ArrayList<>(from.damageDataList.stream().map(DamageData::new).toList());
        this.defendData = new DefendData();
    }
}
