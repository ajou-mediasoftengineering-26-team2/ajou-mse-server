package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 매치 데이터. 내부 DB에 저장되는 Entity.
 *
 * @author Ahn Yubin / 202021088
 */
@Entity
@Getter
@Setter
public class MatchData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    /**
     * 게임 시작 기준 역 ID.
     * TODO: String -> Enum 마이그레이션 고려
     */
    private String station = "<UNKNOWN>";
    /**
     * 선택 마감 시간 (시작).
     */
    private ZonedDateTime countdownStartTime = ZonedDateTime.now();
    /**
     * 선택 마감 시간 (초). 실제 마감 시간은 `countdownStartTime + countdownSec`
     */
    private int countdownSec = 0;
    /**
     * 매치 상태. (e.g. 플레이어 대기, 공/수 손 선택, 플레이어 KO 등)
     */
    private MATCH_STATE state = MATCH_STATE.LOBBY_WAITING;
    /**
     * 승자 플레이어.
     */
    private int winnerPlayerIdx = -1;
    /**
     * 현재 턴. (i.e. 플레이어끼리 티키타카한 횟수)
     */
    private int currentTurn = 0;
    /**
     * 현재 라운드. (i.e. 플레이어끼리 죽고 죽인 횟수)
     */
    private int currentRound = 0;
    /**
     * 현재 "고르는/행동하는" 플레이어 인덱스.
     */
    private int currentPlayerIdx = 0;
    /**
     * 현재 공격수 플레이어 인덱스.
     */
    private int attackerPlayerIdx = 0;
    /**
     * 공격수의 최근 공격이 성공했는지 여부.
     */
    private boolean isAttackSuccess = false;
    /**
     * 현재 매치에 참가중인 플레이어 목록.
     */
    @OneToMany(fetch = FetchType.EAGER)
    private List<PlayerData> players = new ArrayList<>();

    @Transient
    public PlayerData findPlayerById(UUID id) {
        return players.stream()
                .filter(playerData -> playerData.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

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
     * 플레이어를 해당 매치에 추가하거나 수정합니다.
     *
     * @param player 추가 혹은 수정할 플레이어 데이터.
     * @return 플레이어 인덱스.
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
     * UUID에 대응하는 플레이어를 해당 매치로부터 제거합니다.
     *
     * @param id 제거할 플레이어 UUID.
     * @return 정상 제거 여부.
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
}
