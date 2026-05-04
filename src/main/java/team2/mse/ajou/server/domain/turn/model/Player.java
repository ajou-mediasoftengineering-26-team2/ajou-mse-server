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
public class Player {
    @Id
    private String id;

    private String gameMatchId;

    private int hp;
    private int damage;

    private boolean isAttacker;
    private boolean selected;
    private String choice;
}
