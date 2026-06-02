package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkLuck extends Perk {
    private final int bonusCoin = 3;

    public PerkLuck() {
        super(PERK.LUCK);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        PlayerData defender = getDefender(matchData);
        defender.setCoin(defender.getCoin() + bonusCoin);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        if(!isInTurn(matchData)) return false;
        return isDefenseSuccess(matchData) && getDefender(matchData) != null;
    }
}
