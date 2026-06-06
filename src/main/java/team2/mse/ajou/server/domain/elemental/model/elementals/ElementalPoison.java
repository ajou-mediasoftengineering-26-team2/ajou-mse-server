package team2.mse.ajou.server.domain.elemental.model.elementals;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import static java.nio.file.Files.getOwner;

public class ElementalPoison extends Elemental {
    private final int[] maxHpReduceByLevel = {0, 5, 6, 8, 10, 12};

    public ElementalPoison() {
        super(HAND_ELEMENTAL.POISON);
    }


    // 이 친구 구현하는게 좀 애매하네요
    @Override
    public void useElementalIfPossible(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);

        if (isRoundStart(matchData) && hasElemental(owner)) {
            return;
        }

        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);
        int reduceValue = maxHpReduceByLevel[getLevel(owner)];

        opponent.setMaxHp(Math.max(1, opponent.getMaxHp() - reduceValue));
        opponent.setHp(Math.min(opponent.getHp(), opponent.getMaxHp()));

        damageData.addStatusEffect(STATUS_EFFECT.POISON);
        addUsedElemental(damageData);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        PlayerData opponent = getOpponent(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        return isInTurn(matchData)
                && isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && hasElemental(owner)
                && opponent != null
                && damageData != null
                && isFirstDamage(damageData)
                && opponent.getStatusEffectList() != null && !opponent.getStatusEffectList().contains(STATUS_EFFECT.POISON);
    }
}
