package team2.mse.ajou.server.domain.perk.model;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

public abstract class Perk implements IPerk {
    protected final PERK perk;
    protected boolean isUsed;

    public Perk(PERK perk) {
        this.perk = perk;
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        return false;
    }

    protected boolean hasTwoPlayers(MatchData matchData) {
        return matchData != null && matchData.getPlayers() != null && matchData.getPlayers().size() >= 2;
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
        return (matchData.getAttackerPlayerIdx())^1;
    }

    protected PlayerData getDefender(MatchData matchData) {
        int defenderIdx = getDefenderPlayerIdx(matchData);
        if (defenderIdx < 0) {
            return null;
        }
        return matchData.getPlayers().get(defenderIdx);
    }

    /**
     * 라운드 시작, 퍽 획득 직후처럼 공격자/방어자로 소유자를 추론할 수 없는 경우 사용합니다.
     * 호출하는 쪽에서 currentPlayerIdx를 현재 퍽 소유자의 index로 맞춰둔 뒤 호출하면 됩니다.
     */
    protected int getOwnerPlayerIdx(MatchData matchData) {
        if (!hasTwoPlayers(matchData)) {
            return -1;
        }

        int ownerIdx = matchData.getCurrentPlayerIdx();
        if (ownerIdx < 0 || ownerIdx >= matchData.getPlayers().size()) {
            return matchData.getAttackerPlayerIdx();
        }
        return ownerIdx;
    }

    protected PlayerData getOwner(MatchData matchData) {
        int ownerIdx = getOwnerPlayerIdx(matchData);
        if (ownerIdx < 0) {
            return null;
        }
        return matchData.getPlayers().get(ownerIdx);
    }

    protected PlayerData getOpponentOfOwner(MatchData matchData) {
        int ownerIdx = getOwnerPlayerIdx(matchData);
        if (ownerIdx < 0) {
            return null;
        }
        int opponentIdx = (ownerIdx + 1) % matchData.getPlayers().size();
        return matchData.getPlayers().get(opponentIdx);
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
