package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static team2.mse.ajou.server.domain.firebase.FrdbConstants.TIME_FORMATTER;

/**
 * Lobby data used to set FRDB (Firebase Realtime DB). Internally converted from `MatchData`.
 *
 * @author Ahn Yubin / 202021088
 */
@Data
public class FrdbMatchData {
    private String station;
    private String countdownStartTime;
    private int countdownSec;
    private MATCH_STATE state;
    private int winnerPlayerIdx;
    private int currentPlayerIdx;
    private int attackerPlayerIdx;
    private int currentTurn;
    private int currentRound;
    private boolean isAttackSuccess;
    private Map<String, FrdbPlayerData> players;
    private List<FrdbDamageData> damageList;

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
        data.setWinnerPlayerIdx(matchData.getWinnerPlayerIdx());
        data.setCurrentPlayerIdx(matchData.getCurrentPlayerIdx());
        data.setAttackerPlayerIdx(matchData.getAttackerPlayerIdx());
        data.setCurrentTurn(matchData.getCurrentTurn());
        data.setCurrentRound(matchData.getCurrentRound());
        data.setAttackSuccess(matchData.isAttackSuccess());
        data.setPlayers(players);

        data.setDamageList(
                matchData.getDamageDataList()
                        .stream()
                        .map(FrdbDamageData::from)
                        .toList()
        );

        return data;
    }
}
