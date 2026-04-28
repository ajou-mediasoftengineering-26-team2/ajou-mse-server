package team2.mse.ajou.server.domain.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class LobbyData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToMany
    private List<PlayerData> players = new ArrayList<>();
    private LobbyState state = LobbyState.WAITING; // 현재 상태
    private int currentTurn = 0; // 현재 턴 (i.e. 플레이어끼리 티키타카한 횟수)
    private int currentPlayer = 0; // 현재 "고르는/행동하는" 플레이어 인덱스
    private ZonedDateTime countdownStartTime = ZonedDateTime.now(); // 선택 마감 시간 (시작)
    private int countdownSec = 0; // 선택 마감 시간 (초)
}
