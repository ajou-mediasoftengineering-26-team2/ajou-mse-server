package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.modellisteners.MatchDataJpaListener;
import team2.mse.ajou.server.domain.shared.match.service.MatchRunnerService;

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

    @OneToMany(fetch = FetchType.EAGER)
    private List<PlayerData> players = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("damageIndex ASC")
    private List<DamageData> damageDataList = new ArrayList<>();


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

    @Transient
    public void addDamageData(DamageData damageData) {
        damageDataList.add(damageData);
    }

    @Transient
    public void clearDamageDataList() {
        if(damageDataList == null) {
            damageDataList = new ArrayList<>();
        }
        damageDataList.clear();
    }
}
