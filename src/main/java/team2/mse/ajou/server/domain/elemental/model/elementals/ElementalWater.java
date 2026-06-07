package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class ElementalWater extends Elemental {
    private final int[] healByLevel = {0, 5, 7, 9, 12, 15};

    public ElementalWater() {
        super(HAND_ELEMENTAL.WATER);
    }

    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DefendData defendData = matchData.getDefendData();

        defendData.setRecoveredHp(defendData.getRecoveredHp() + healByLevel[getLevel(owner)]);
        defendData.setUsedElemental(elemental);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);

        return isInTurn(matchData)
                && isDefenseSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && hasElemental(owner)
                && matchData.getDefendData() != null;
    }
}
