package team2.mse.ajou.server.domain.turn.service;

import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.model.Damage;
import team2.mse.ajou.server.domain.turn.model.DefendEffect;

import java.util.List;

public interface IDamageCalcService {
    List<Damage> calcDamageList(PlayerData attacker, PlayerData defender);
    List<DefendEffect> calcDefendList(PlayerData defender);
}
