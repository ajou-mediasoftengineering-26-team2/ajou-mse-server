package team2.mse.ajou.server.domain.shared.match.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * `RunningMatch` -> 외부 (`MatchRunnerService`)로 나가는 콜백. 예를 들어 데이터 가져오기, 데이터 수정 후 확정(?), state 변경 등
 *
 * @author Ahn Yubin / 202021088
 */
public interface IMatchDataDelegate {
    void updateMatchDataForRoundBegin(MatchData matchData);

    void updateMatchDataForTurnBegin(MatchData matchData);

    void calculateTurn(MatchData matchData);

    void receiveItemForAllPlayers(MatchData matchData);

    void setPerkChoiceForAllPlayers(MatchData matchData);

    ScheduledFuture<?> setTimerAndRun(UUID matchId, int seconds, Runnable callback);

    Optional<MatchData> getMatchData(UUID id);

    Optional<PlayerData> getPlayerData(UUID id);

    void commitMatchData(MatchData data);

    void commitPlayerData(PlayerData data);

    void commitFrdbData(MatchData matchData);
}
