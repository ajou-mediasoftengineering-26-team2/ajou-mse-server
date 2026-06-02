package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkRich extends Perk {
    private final int coinValue = 30;

    public PerkRich() {
        super(PERK.RICH);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        PlayerData owner = getOwner(matchData);
        owner.setCoin(owner.getCoin() + coinValue);
        isUsed = true;
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        return !isUsed
                && matchData != null
                && matchData.getState() == MATCH_STATE.GAME_PERK_ITEM_RECEIVING
                && getOwner(matchData) != null;
    }
}
