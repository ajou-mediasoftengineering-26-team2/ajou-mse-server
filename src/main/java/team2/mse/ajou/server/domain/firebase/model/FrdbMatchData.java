package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.Map;
import java.util.stream.Collectors;

import static team2.mse.ajou.server.domain.firebase.FrdbConstants.TIME_FORMATTER;

/**
 * Firebase RDB에 저장할 때 사용되는 로비 정보. 실제로는 MatchData 클래스를 사용하니 해당 클래스를 참고바랍니다.
 *
 * @author Ahn yubin / 202021088
 */
@Data
public class FrdbMatchData {
    private String station;
    private String countdownStartTime;
    private int countdownSec;
    private MATCH_STATE state;
    private int currentPlayerIdx;
    private int attackerPlayerIdx;
    private int currentTurn;
    private int currentRound;
    private Map<String, FrdbPlayerData> players;

    // 내부적인 LobbyData -> FRDB 로비 정보 변환
    public static FrdbMatchData from(MatchData matchData) {
        Map<String, FrdbPlayerData> players = matchData
                .getPlayers()
                .stream()
                .collect(Collectors.toMap(playerData -> playerData.getId().toString(), FrdbPlayerData::from));

        FrdbMatchData data = new FrdbMatchData();

        data.setStation(matchData.getStation());
        data.setCountdownStartTime(matchData.getCountdownStartTime().format(TIME_FORMATTER));
        data.setCountdownSec(matchData.getCountdownSec());
        data.setState(matchData.getState());
        data.setCurrentPlayerIdx(matchData.getCurrentPlayerIdx());
        data.setAttackerPlayerIdx(matchData.getAttackerPlayerIdx());
        data.setCurrentTurn(matchData.getCurrentTurn());
        data.setCurrentRound(matchData.getCurrentRound());
        data.setPlayers(players);

        return data;
    }
}
