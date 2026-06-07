package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;

public class ElementalWind extends Elemental {
    private final int[] dodgeCountByLevel = {0, 1, 2, 2, 3, 4};
    private final int dodgeCoinBonus = 5;

    public ElementalWind() {
        super(HAND_ELEMENTAL.WIND);
    }

    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);

        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.setDamage(0);
        damageData.setCoin(0);
        damageData.setAttackType(ATTACK_TYPE.MISS);
        addUsedElemental(damageData);

        owner.setDodgeCount(owner.getDodgeCount() + 1);

        if (getLevel(owner) >= 3) {
            owner.setCoin(owner.getCoin() + dodgeCoinBonus);
            damageData.addCoin(dodgeCoinBonus);
        }
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        int level = getLevel(owner);

        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && hasElemental(owner)
                && owner.getDodgeCount() < dodgeCountByLevel[level]
                && damageData != null
                && isFirstDamage(damageData);
    }
}
