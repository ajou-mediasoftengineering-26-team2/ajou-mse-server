package team2.mse.ajou.server.domain.perk.model.perks;

import team2.mse.ajou.server.domain.perk.model.Perk;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Perk - 유연한 손
 * 찌르기로 가만히, 가만히로 찌르기 방어 가능
 * Can defend the INSERT_BETWEEN_HANDS with the SHAKE_OVER_HANDS,
 * and the SHAKE_OVER_HANDS with the INSERT_BETWEEN_HANDS.
 * @author Junseo Hwang 202322128
 */
public class PerkFlexibleMind extends Perk {
    public PerkFlexibleMind() {
        super(PERK.FLEXIBLE_MIND);
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
                && isInsertShakePair(attacker.getChoice(), owner.getChoice());
    }

    private boolean isInsertShakePair(HAND_CHOICE attackerChoice, HAND_CHOICE defenderChoice) {
        return (attackerChoice == HAND_CHOICE.INSERT_BETWEEN_HANDS
                && defenderChoice == HAND_CHOICE.SHAKE_OVER_HANDS)
                || (attackerChoice == HAND_CHOICE.SHAKE_OVER_HANDS
                && defenderChoice == HAND_CHOICE.INSERT_BETWEEN_HANDS);
    }
}
