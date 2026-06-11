package team2.mse.ajou.server.domain.shared.match.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public interface IMatchTurnCalcService {
    void updateMatchDataForRoundBegin(MatchData matchData);

    void updateMatchDataForTurnBegin(MatchData matchData);

    void calculateTurn(MatchData matchData);
}
