package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class ElementalLightning extends Elemental {
    private final int[] bonusDamageByLevel = {0, 2, 3, 4, 5, 6};

    public ElementalLightning() {
        super(HAND_ELEMENTAL.LIGHTNING);
    }

    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        damageData.addDamage(bonusDamageByLevel[getLevel(owner)]);
        addUsedElemental(damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && hasElemental(owner)
                && damageData != null
                && isFirstDamage(damageData);
    }
}
