package team2.mse.ajou.server.domain.turn.service;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.turn.model.DefendEffect;

import java.util.List;

public interface IDamageCalcService {
    void calcDamageList(MatchData matchData);
    void calcDefendEffect(MatchData matchData);
}
