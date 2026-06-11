package team2.mse.ajou.server.domain.shared.match.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * Delegate 'holder' for logics that accesses repositories
 * (i.e. way for `RunningMatch` to access `MatchRunnerService`'s repository via exposed functions to switch state, save data etc)
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
