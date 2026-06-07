package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 도발
 * 라운드 시작시 상대 HP -5
 * @author Junseo Hwang 202322128
 */
public class PerkTaunt extends Perk {
    private final int damageValue = 5;

    public PerkTaunt() {
        super(PERK.TAUNT);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        opponent.setHp(Math.max(0, opponent.getHp() - damageValue));
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        return isRoundStart(matchData) && getOpponent(matchData, ownerPlayerIdx) != null;
    }
}

