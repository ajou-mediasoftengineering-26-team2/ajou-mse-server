package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkTaunt extends Perk {
    private final int damageValue = 5;

    public PerkTaunt() {
        super(PERK.TAUNT);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        PlayerData opponent = getOpponentOfOwner(matchData);
        opponent.setHp(Math.max(0, opponent.getHp() - damageValue));
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        return isRoundStart(matchData) && getOpponentOfOwner(matchData) != null;
    }
}
