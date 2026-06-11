package team2.mse.ajou.server.domain.elemental.model;

import lombok.Getter;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * 로직 계산을 위한 Elemental 클래스
 * Class for elemental logic calculation
 *
 * @author Junseo Hwang 202322128
 */
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

    /**
     * 두 명의 플레이어가 모두 있는지
     * This checks whether two player exist
     * @param matchData current match
     * @return if two player exist, return true.
     */
    protected boolean hasTwoPlayers(MatchData matchData) {
        return matchData != null && matchData.getPlayers() != null && matchData.getPlayers().size() >= 2;
    }

    /**
     * 플레이어 인덱스가 옳은 인덱스인지
     * This checks whether player index is valid
     * @param matchData current match
     * @param ownerPlayerIdx player index to check
     * @return if index is valid, return true
     */
    protected boolean isValidOwnerIdx(MatchData matchData, int ownerPlayerIdx) {
        return hasTwoPlayers(matchData)
                && ownerPlayerIdx >= 0
                && ownerPlayerIdx < matchData.getPlayers().size();
    }

    /**
     * elemental의 주인인지 확인
     * This checks whether player[player idx] is owner
     * @param matchData current match
     * @param ownerPlayerIdx player index to check
     * @return if player[player idx] is owner, return true
     */
    protected PlayerData getOwner(MatchData matchData, int ownerPlayerIdx) {
        if (!isValidOwnerIdx(matchData, ownerPlayerIdx)) {
            return null;
        }
        return matchData.getPlayers().get(ownerPlayerIdx);
    }

    /**
     * player[owner player idx]의 상대방을 가져옴
     * get Opponent of player[owner player idx]
     * @param matchData current match
     * @param ownerPlayerIdx owner index
     * @return PlayerData of Opponent of player[owner player idx]
     */
    protected PlayerData getOpponent(MatchData matchData, int ownerPlayerIdx) {
        if (!isValidOwnerIdx(matchData, ownerPlayerIdx)) {
            return null;
        }
        return matchData.getPlayers().get(ownerPlayerIdx ^ 1);
    }

    /**
     * 매치의 방어자를 가져옴
     * get Defender in the match
     * @param matchData current match
     * @return index of defender in the match
     */
    protected int getDefenderPlayerIdx(MatchData matchData) {
        if (!hasTwoPlayers(matchData)) {
            return -1;
        }
        return matchData.getAttackerPlayerIdx() ^ 1;
    }

    /**
     * idx의 플레이어가 공격자인지
     * This checks whether player[idx] is attacker
     * @param matchData current match
     * @param ownerPlayerIdx index of player to check whether he is attacker
     * @return if player[idx] is attacker, return true
     */
    protected boolean isOwnerAttacker(MatchData matchData, int ownerPlayerIdx) {
        return matchData != null && matchData.getAttackerPlayerIdx() == ownerPlayerIdx;
    }

    /**
     * idx의 플레이어가 방어자인지
     * This checks whether player[idx] is defender
     * @param matchData current match
     * @param ownerPlayerIdx index of player to check whether he is defender
     * @return if player[idx] is defender, return true
     */
    protected boolean isOwnerDefender(MatchData matchData, int ownerPlayerIdx) {
        return getDefenderPlayerIdx(matchData) == ownerPlayerIdx;
    }

    /**
     * 공격에 성공했는지
     * @param matchData current match
     * @return if attack success, return true
     */
    protected boolean isAttackSuccess(MatchData matchData) {
        return matchData != null && matchData.isAttackSuccess();
    }

    /**
     *
     * @param matchData current match
     * @return if defenseSuccess, return true
     */
    protected boolean isDefenseSuccess(MatchData matchData) {
        return matchData != null && !matchData.isAttackSuccess();
    }

    /**
     * 턴(공격 방어) 중인지
     * This checks whether match is in turn
     * @param matchData match to check
     * @return if match is in turn(MATCH_STATE == choice finished), return true
     */
    protected boolean isInTurn(MatchData matchData) {
        return matchData != null && matchData.getState() == MATCH_STATE.GAME_CHOICE_FINISHED;
    }

//    protected boolean isRoundStart(MatchData matchData) {
//        return matchData != null && matchData.getState() == MATCH_STATE.GAME_ROUND_START_ANIMATION;
//    }

    /**
     * 데미지 리스트의 마지막 데미지 데이터를 가져옴.
     * @param matchData current match
     * @return Damage Data of Damage list's last entry
     */
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

    /**
     * 이 데미지 데이터가 첫번째 타격의 데미지 데이터인지
     * This checks whether this attack is the first attack
     * @param damageData damage data of attack to check whether this attack is the first attack
     * @return if attack is the first attack, return true
     */
    protected boolean isFirstDamage(DamageData damageData) {
        return damageData != null && damageData.getDamageIndex() == 0;
    }

    /**
     * 플레이어가 현재 elemental을 가지고 있는지
     * @param owner player to check whether he has elemental
     * @return if player has elemental, return true
     */
    protected boolean hasElemental(PlayerData owner) {
        return owner != null && owner.getHandElemental() == elemental;
    }

    /**
     * 플레이어의 elemental level
     * player's elemental level
     * @param owner player
     * @return elemental level
     */
    protected int getLevel(PlayerData owner) {
        if (owner == null) {
            return 1;
        }
        return Math.max(1, Math.min(5, owner.getElementalLevel()));
    }

    /**
     * 데미지 데이터에 현재 elemental을 사용했다는 정보를 기록함
     * At Damage data, this records that current elemental was used on attack
     * @param damageData damage data to store that elemental was used
     */
    protected void addUsedElemental(DamageData damageData) {
        if (damageData != null) {
            damageData.setHandElemental(elemental);
        }
    }

    /**
     * Don't use this!!
     * This function may cause healing to be applied multiple times.
     * @param player
     * @param damageData
     * @param healValue
     */
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