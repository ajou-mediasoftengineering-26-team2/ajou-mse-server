package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

public class PerkWiseInvestor extends Perk {
    private final int moduloValue = 10;

    private PlayerData attacker;

    public PerkWiseInvestor() {
        super(PERK.WISE_INVESTOR);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if(isAvailable(matchData)) {
            DamageData damageData = matchData.getDamageDataList().getLast();
            damageData.addDamage(attacker.getCoin()/moduloValue);
            damageData.addUsedPerk(perk);

            isUsed = true;
        }
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        if(!isInTurn(matchData)) return false;

        attacker = matchData.getPlayers().get(matchData.getAttackerPlayerIdx());
        return !isUsed && attacker.getCoin()>moduloValue;
    }
}
