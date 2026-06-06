package team2.mse.ajou.server.domain.elemental.model;

import lombok.Getter;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

@Getter
public abstract class Elemental implements IElemental {
    protected final HAND_ELEMENTAL elemental;

    protected Elemental(HAND_ELEMENTAL elemental) {
        this.elemental = elemental;
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

    protected int getDefenderPlayerIdx(MatchData matchData) {
        if (!hasTwoPlayers(matchData)) {
            return -1;
        }
        return matchData.getAttackerPlayerIdx() ^ 1;
    }

    protected boolean isOwnerAttacker(MatchData matchData, int ownerPlayerIdx) {
        return matchData != null && matchData.getAttackerPlayerIdx() == ownerPlayerIdx;
    }

    protected boolean isOwnerDefender(MatchData matchData, int ownerPlayerIdx) {
        return getDefenderPlayerIdx(matchData) == ownerPlayerIdx;
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

    protected boolean hasElemental(PlayerData owner) {
        return owner != null && owner.getHandElemental() == elemental;
    }

    protected int getLevel(PlayerData owner) {
        if (owner == null) {
            return 1;
        }
        return Math.max(1, Math.min(5, owner.getElementalLevel()));
    }

    protected void addUsedElemental(DamageData damageData) {
        if (damageData != null) {
            damageData.setHandElemental(elemental);
        }
    }

    protected void heal(PlayerData player, DamageData damageData, int healValue) {
        if (player == null) {
            return;
        }

        // player max hp만들면 min(maxHp, __) 걸어야함
        int beforeHp = player.getHp();
        player.setHp(player.getHp() + healValue);

        if (damageData != null) {
            damageData.addRecoveredHp(player.getHp() - beforeHp);
        }
    }
}