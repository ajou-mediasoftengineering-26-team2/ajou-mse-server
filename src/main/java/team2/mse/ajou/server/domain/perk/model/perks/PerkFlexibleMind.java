package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkFlexibleMind extends Perk {
    public PerkFlexibleMind() {
        super(PERK.FLEXIBLE_MIND);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData) {
        if (!isAvailable(matchData)) {
            return;
        }

        matchData.setAttackSuccess(false);
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        PlayerData attacker = getAttacker(matchData);
        PlayerData defender = getDefender(matchData);

        return isAttackSuccess(matchData)
                && attacker != null
                && defender != null
                && isInsertShakePair(attacker.getChoice(), defender.getChoice());
    }

    private boolean isInsertShakePair(HAND_CHOICE attackerChoice, HAND_CHOICE defenderChoice) {
        return (attackerChoice == HAND_CHOICE.INSERT_BETWEEN_HANDS
                && defenderChoice == HAND_CHOICE.SHAKE_OVER_HANDS)
                || (attackerChoice == HAND_CHOICE.SHAKE_OVER_HANDS
                && defenderChoice == HAND_CHOICE.INSERT_BETWEEN_HANDS);
    }
}
