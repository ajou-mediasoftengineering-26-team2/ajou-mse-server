package team2.mse.ajou.server.domain.firebase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.MatchState;

import java.util.Map;
import java.util.stream.Collectors;

import static team2.mse.ajou.server.domain.firebase.FrdbConstants.TIME_FORMATTER;

/**
 * Firebase RDB에 저장할 때 사용되는 로비 정보.
 *
 * @author yubin
 */
@AllArgsConstructor
@ToString
@Getter
public class FrdbMatchData {
    private Map<String, FrdbPlayerData> players;
    private MatchState state; // 현재 상태
    private int currentTurn; // 현재 턴 (i.e. 플레이어끼리 티키타카한 횟수)
    private int currentPlayer; // 현재 "고르는/행동하는" 플레이어
    private String countdownStartTime; // 선택 마감 시간 (시작)
    private int countdownSec; // 선택 마감 시간 (초)

    // 내부적인 LobbyData -> FRDB 로비 정보 변환
    public static FrdbMatchData from(MatchData matchData) {
        Map<String, FrdbPlayerData> players = matchData
                .getPlayers()
                .stream()
                .collect(Collectors.toMap(playerData -> playerData.getId().toString(), FrdbPlayerData::from));

        return new FrdbMatchData(
                players,
                matchData.getState(),
                matchData.getCurrentTurn(),
                matchData.getCurrentPlayer(),
                // lobbyData.getCountdownStartTime().toLocalDateTime(),
                matchData.getCountdownStartTime().format(TIME_FORMATTER),
                matchData.getCountdownSec()
        );
    }
}
