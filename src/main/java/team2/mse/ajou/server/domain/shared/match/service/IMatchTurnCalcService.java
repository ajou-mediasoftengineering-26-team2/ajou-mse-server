package team2.mse.ajou.server.domain.shared.match.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * Service that modifies `MatchData` to calculate a turn, setup round/turn.
 *
 * @author Ahn Yubin / 202021088
 */
public interface IMatchTurnCalcService {
    void updateMatchDataForRoundBegin(MatchData matchData);

    void updateMatchDataForTurnBegin(MatchData matchData);

    void calculateTurn(MatchData matchData);
}
