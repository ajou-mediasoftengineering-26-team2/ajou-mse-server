package team2.mse.ajou.server.domain.elemental.model;

import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public interface IElemental {
    void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx);
    boolean isAvailable(MatchData matchData, int ownerPlayerIdx);
}