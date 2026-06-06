package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * @author Junseo Hwang 202322128
 */
public abstract class Perk implements IPerk {
    protected final PERK perk;
    protected boolean isUsed;

    public Perk(PERK perk) {
        this.perk = perk;
    }

    @Override
    public boolean isAvailable(MatchData matchData, int ownerPlayerIdx) {
        return false;
    }

    protected boolean hasTwoPlayers(MatchData matchData) {
        return matchData != null && matchData.getPlayers() != null && matchData.getPlayers().size() >= 2;
    }

    protected boolean isValidOwnerIdx(MatchData matchData, int ownerPlayerIdx) {
        return hasTwoPlayers(matchData)
                && ownerPlayerIdx >= 0
                && ownerPlayerIdx < matchData.getPlayers().size();
    }

    protected PlayerData getOwner(MatchData matchData, int ownerPlayerIdx) {
        if (!isValidOwnerIdx(matchData, ownerPlayerIdx)) {
            return null;
        }
        return matchData.getPlayers().get(ownerPlayerIdx);
    }

    protected PlayerData getOpponent(MatchData matchData, int ownerPlayerIdx) {
        if (!isValidOwnerIdx(matchData, ownerPlayerIdx)) {
            return null;
        }
        return matchData.getPlayers().get(ownerPlayerIdx ^ 1);
    }

    protected PlayerData getAttacker(MatchData matchData) {
        if (!hasTwoPlayers(matchData)) {
            return null;
        }
        return matchData.getPlayers().get(matchData.getAttackerPlayerIdx());
    }

    protected int getDefenderPlayerIdx(MatchData matchData) {
        if (!hasTwoPlayers(matchData)) {
            return -1;
        }
        return matchData.getAttackerPlayerIdx() ^ 1;
    }

    protected PlayerData getDefender(MatchData matchData) {
        int defenderIdx = getDefenderPlayerIdx(matchData);
        if (defenderIdx < 0) {
            return null;
        }
        return matchData.getPlayers().get(defenderIdx);
    }

    protected boolean isOwnerAttacker(MatchData matchData, int ownerPlayerIdx) {
        return matchData != null && matchData.getAttackerPlayerIdx() == ownerPlayerIdx;
    }

    protected boolean isOwnerDefender(MatchData matchData, int ownerPlayerIdx) {
        return getDefenderPlayerIdx(matchData) == ownerPlayerIdx;
    }

    protected DamageData getCurrentDamageData(MatchData matchData) {
        if (matchData == null) {
            return null;
        }

        List<DamageData> damageDataList = matchData.getDamageDataList();
        if (damageDataList == null || damageDataList.isEmpty()) {
            return null;
        }
        return damageDataList.getLast();
    }

    protected boolean isFirstDamage(DamageData damageData) {
        return damageData != null && damageData.getDamageIndex() == 0;
    }

    protected boolean isAttackSuccess(MatchData matchData) {
        return matchData != null && matchData.isAttackSuccess();
    }

    protected boolean isDefenseSuccess(MatchData matchData) {
        return matchData != null && !matchData.isAttackSuccess();
    }

    protected boolean isInTurn(MatchData matchData) {
        return matchData != null && matchData.getState() == MATCH_STATE.GAME_CHOICE_FINISHED;
    }

    protected boolean isRoundStart(MatchData matchData) {
        return matchData != null && matchData.getState() == MATCH_STATE.GAME_ROUND_START_ANIMATION;
    }
}
