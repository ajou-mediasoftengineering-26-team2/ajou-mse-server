package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;

import java.util.UUID;

/**
 * Player data. Entity saved to internal DB.
 *
 * @author Ahn Yubin / 202021088
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
}
