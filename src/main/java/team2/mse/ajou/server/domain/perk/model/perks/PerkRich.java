package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 부자
 * 즉시 코인 +30 획득
 * @author Junseo Hwang 202322128
 */
public class PerkRich extends Perk {
    private final int coinValue = 30;

    public PerkRich() {
        super(PERK.RICH);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        owner.setCoin(owner.getCoin() + coinValue);
        isUsed = true;
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        return !isUsed
                && matchData != null
                && matchData.getState() == MATCH_STATE.GAME_PERK_ITEM_RECEIVING
                && getOwner(matchData, ownerPlayerIdx) != null;
    }
}
