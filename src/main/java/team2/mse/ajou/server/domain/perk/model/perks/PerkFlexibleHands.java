package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

public class PerkFlexibleHands extends Perk {
    public PerkFlexibleHands() {
        super(PERK.FLEXIBLE_HANDS);
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
                && isLeftRightPair(attacker.getChoice(), defender.getChoice());
    }

    private boolean isLeftRightPair(HAND_CHOICE attackerChoice, HAND_CHOICE defenderChoice) {
        return (attackerChoice == HAND_CHOICE.SINGLE_HAND_FLIP_LEFT
                && defenderChoice == HAND_CHOICE.SINGLE_HAND_FLIP_RIGHT)
                || (attackerChoice == HAND_CHOICE.SINGLE_HAND_FLIP_RIGHT
                && defenderChoice == HAND_CHOICE.SINGLE_HAND_FLIP_LEFT);
    }
}
