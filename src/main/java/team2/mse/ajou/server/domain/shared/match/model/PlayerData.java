package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * 플레이어 데이터. 내부 DB에 저장되는 Entity.
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
     * 현재 참여중인 매치 ID.
     */
    private UUID joinedMatchId;
    /**
     * 사용자 이름.
     */
    @Column(unique = true)
    private String username;
    /**
     * 승리 (킬) 횟수.
     */
    private int wins = 0;
    /**
     * 체력.
     */
    private int hp = 10;
    /**
     * (대기방) 준비 여부.
     */
    private boolean isReady = false;
    /**
     * (현재 턴) 공격 여부.
     */
    private boolean isAttacking = false;
    /**
     * (현재 턴) 손(?) 선택 여부.
     */
    private boolean isSelecting = false;
}
