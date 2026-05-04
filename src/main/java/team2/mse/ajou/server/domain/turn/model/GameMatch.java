package team2.mse.ajou.server.domain.turn.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


/**
 * @author Junseo Hwang
 */

@Entity
@Getter
@Setter
public class GameMatch {
    @Id
    private String id;

    private String player1Id;
    private String player2Id;

    private String AttackerId;
    private int turnNumber;
}
