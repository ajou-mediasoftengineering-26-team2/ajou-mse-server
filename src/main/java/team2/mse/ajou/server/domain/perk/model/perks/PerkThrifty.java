package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

public class PerkThrifty extends Perk {
    private final int bonusValue = 2;

    public PerkThrifty() {
        super(PERK.THRIFTY);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        DamageData damageData = getCurrentDamageData(matchData);
        damageData.addCoin(bonusValue);
        damageData.addUsedPerk(perk);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        // 일단은 turn 공격의 코인만
        if(!isInTurn(matchData)) return false;
        DamageData damageData = getCurrentDamageData(matchData);
        return damageData != null && damageData.getCoin() > 0;
    }
}
