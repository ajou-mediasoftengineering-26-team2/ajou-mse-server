package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 도발
 * 방어에 성공 시 상대 HP -5
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

        DefendData defendData = matchData.getDefendData();
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        opponent.setHp(Math.max(0, opponent.getHp() - damageValue));
        defendData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        return isDefenseSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && matchData.getDefendData() != null;
    }
}

