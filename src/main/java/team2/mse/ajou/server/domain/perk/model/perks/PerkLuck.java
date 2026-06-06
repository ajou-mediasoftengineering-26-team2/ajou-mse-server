package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 행운
 * 방어 성공 시 추가 코인 +3
 * @author Junseo Hwang 202322128
 */
public class PerkLuck extends Perk {
    private final int bonusCoin = 3;

    public PerkLuck() {
        super(PERK.LUCK);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        DefendData defendData = matchData.getDefendData();
        defendData.addCoin(bonusCoin);
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
