package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class PlayerData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID joinedMatchId;
    @Column(unique = true)
    private String username;
    private int wins = 0;
    private int hp = 10;
    private boolean isReady = false;
    private boolean isAttacking = false;
    private boolean isSelecting = false;
}
