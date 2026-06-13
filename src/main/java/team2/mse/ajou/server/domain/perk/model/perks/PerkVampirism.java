package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 흡혈귀
 * 첫번째 공격 성공시 HP +3 회복
 * Restores 3 HP when the first attack succeeds.
 * @author Junseo Hwang 202322128
 */
public class PerkVampirism extends Perk {
    private final int healValue = 3;

    public PerkVampirism() {
        super(PERK.VAMPIRISM);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        PlayerData owner = getOwner(matchData, ownerPlayerIdx);
        DamageData damageData = getCurrentDamageData(matchData);

        damageData.addRecoveredHp(healValue);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        DamageData damageData = getCurrentDamageData(matchData);
        return isAttackSuccess(matchData)
                && isOwnerAttacker(matchData, ownerPlayerIdx)
                && getOwner(matchData, ownerPlayerIdx) != null
                && isFirstDamage(damageData);
    }
}
