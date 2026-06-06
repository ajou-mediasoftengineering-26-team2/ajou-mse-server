package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class ElementalPlant extends Elemental {
    private final int[] maxHpBonusByLevel = {0, 15, 20, 25, 30, 50};

    public ElementalPlant() {
        super(HAND_ELEMENTAL.PLANT);
    }

    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        // 초기 최대 hp 설정해야함.
        owner.setMaxHp(10 + maxHpBonusByLevel[getLevel(owner)]);
        owner.setHp(owner.getMaxHp());
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        return isRoundStart(matchData) && hasElemental(owner);
    }
}
