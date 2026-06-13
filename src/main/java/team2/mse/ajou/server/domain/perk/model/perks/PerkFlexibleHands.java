package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 유연한 손
 * 왼손으로 오른손, 오른손으로 왼손 방어 가능
 * Can defend the right hand with the left hand, and the left hand with the right hand.
 * @author Junseo Hwang 202322128
 */
public class PerkFlexibleHands extends Perk {
    public PerkFlexibleHands() {
        super(PERK.FLEXIBLE_HANDS);
    }

    @Override
    public void usePerkIfPossible(MatchData matchData, int ownerPlayerIdx) {
        if (!isAvailable(matchData, ownerPlayerIdx)) {
            return;
        }

        matchData.setAttackSuccess(false);
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        if (!isInTurn(matchData)) return false;

        PlayerData attacker = getAttacker(matchData);
        PlayerData owner = getOwner(matchData, ownerPlayerIdx);

        return isAttackSuccess(matchData)
                && isOwnerDefender(matchData, ownerPlayerIdx)
                && attacker != null
                && owner != null
                && isLeftRightPair(attacker.getChoice(), owner.getChoice());
    }

    private boolean isLeftRightPair(HAND_CHOICE attackerChoice, HAND_CHOICE defenderChoice) {
        return (attackerChoice == HAND_CHOICE.SINGLE_HAND_FLIP_LEFT
                && defenderChoice == HAND_CHOICE.SINGLE_HAND_FLIP_RIGHT)
                || (attackerChoice == HAND_CHOICE.SINGLE_HAND_FLIP_RIGHT
                && defenderChoice == HAND_CHOICE.SINGLE_HAND_FLIP_LEFT);
    }
}
