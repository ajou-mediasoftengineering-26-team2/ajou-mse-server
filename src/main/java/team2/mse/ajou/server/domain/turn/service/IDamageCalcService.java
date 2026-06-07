package team2.mse.ajou.server.domain.turn.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public interface IDamageCalcService {
    void calcDamageList(MatchData matchData);
    void calcDefendEffect(MatchData matchData);
}
