package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.perk.model.IPerk;
import team2.mse.ajou.server.domain.perk.service.PerkFactory;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.service.IDamageCalcService;

import java.util.List;
import java.util.Random;

/**
 * Match turn/game logic calculation handling service.
 *
 * @author Ahn Yubin / 202021088
 * @author Junseo Hwang 202322128
 */
@Service
public class MatchTurnCalcService {
    private final Random attackerRandom;
    private final IDamageCalcService damageCalcService;

    public MatchTurnCalcService(IDamageCalcService damageCalcService) {
        this.damageCalcService = damageCalcService;
        this.attackerRandom = new Random(System.currentTimeMillis());
    }

    /**
     * 주어진 `MatchData`를 라운드 시작 상태로 수정시킵니다.
     *
     * @param matchData Match data to be modified.
     */
    public void updateMatchDataForRoundBegin(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();

        if (players.size() < 2) {
            //throw new ApiError(5005, "Insufficient players in the match!");
            System.err.printf("[MATCH_TURN_CALC] updateMatchDataForRoundBegin(MATCH: %s) | Insufficient players in the match! (%d players)\n", matchData.getId(), players.size());
            return;
        }

        // Random player attacks
        int attackerIdx = attackerRandom.nextInt(0, players.size());

        // Reset HP and turn state. (Both players!!)
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            player.setHp(10);
            player.setFinalWinner(false);
            player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            player.setAckState(ACK_TYPE.NO_ACK);
            player.setSelecting(true);
            player.setAttacking(i == attackerIdx);
        }

        matchData.setAttackerPlayerIdx(attackerIdx);
        matchData.setAttackSuccess(false);

        // DamageList를 초기에 설정해야할지도 모르겠습니다.
        matchData.clearDamageDataList();
        matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);
    }

    /**
     * 주어진 `MatchData`를 턴 시작 상태로 수정시킵니다.
     *
     * @param matchData Match data to be modified.
     */
    public void updateMatchDataForTurnBegin(MatchData matchData) {
        // 이전 턴의 공격이 실패한 경우 공수 교대...
        if(!matchData.isAttackSuccess()){
            matchData.setAttackerPlayerIdx(matchData.getAttackerPlayerIdx()^1);
        }

        List<PlayerData> players = matchData.getPlayers();
        int attackerIdx = matchData.getAttackerPlayerIdx();

        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            player.setAckState(ACK_TYPE.NO_ACK);
            player.setSelecting(true);
            player.setAttacking(i == attackerIdx);

            if (matchData.getCurrentTurn() == 0) {
                player.getStatusEffectList().clear();
            }
        }

        matchData.setState(MATCH_STATE.GAME_PLAYER_CHOICE);
        matchData.clearDamageDataList();
        matchData.setAttackSuccess(false);
        matchData.setKo(false);
    }

    /**
     * Calculates a single turn from given `MatchData`.
     * choice 상태에서 5초가 끝나면 finished 상태로 전환합니다.
     * 데미지 계산은 두 클라이언트의 /turn/choice 요청이 모두 들어온 뒤 실행합니다.
     *
     * @param matchData Match data to be modified.
     */
    public void calculateTurn(MatchData matchData) {

        MATCH_STATE state = matchData.getState();
        List<PlayerData> players = matchData.getPlayers();

        if (players.size() < 2) {
            throw new ApiError(5005, "Insufficient players in the match!");
        }

        if (state == MATCH_STATE.GAME_PLAYER_CHOICE) {
            // 이 if문으로 들어왔다는 것은 5초가 지나서 choice가 끝났다는 것을 의미합니다.
            // finished가 되면 클라이언트는 choice결과를 /turn/choice로 보내게 됩니다.
            matchData.setState(MATCH_STATE.GAME_CHOICE_FINISHED);
            // Damage 초기화
            return;
        }

        int attackerIdx = matchData.getAttackerPlayerIdx();
        int defenceIdx = (attackerIdx + 1) % players.size();

        System.out.printf("\t[calculateTurn @ %s] BEFORE ATTACKER IDX: %d, DEFENDER IDX: %d\n", matchData.getId(), attackerIdx, defenceIdx);

        // Attacking player reference.
        PlayerData attackerPlayer = players.get(attackerIdx);
        HAND_CHOICE attackerChoice = attackerPlayer.getChoice();
        // Defending player reference.
        PlayerData defencePlayer = players.get(defenceIdx);
        HAND_CHOICE defenceChoice = defencePlayer.getChoice();

        // Has attacking player KO'd the defending player?
        boolean isPlayerKO = false;

        boolean isAttackSuccess = attackerChoice != defenceChoice;
        matchData.setAttackSuccess(isAttackSuccess);
        applyPerksInAttackSuccessDecision(matchData, defenceIdx);

        isAttackSuccess = matchData.isAttackSuccess();
        matchData.setAttackSuccess(isAttackSuccess);

        // BEGIN DAMAGE CALCULATION LOGIC --------------------------
        // TODO: ADD ON-DAMAGE PERK EFFECTS ETC
        if (isAttackSuccess) {
            // Deal damage to defending player.
            // FIXME: CONSTANT DAMAGE (2) FOR NOW.
//            int damageAmount = 2;
//            defencePlayer.setHp(Math.max(0, defencePlayer.getHp() - damageAmount));
            damageCalcService.calcDamageList(matchData);

            isPlayerKO = (defencePlayer.getHp() <= 0);
        } else {
            // Defending success! Switch the roles around.
            // switch attackerIdx and defenceIdx
            damageCalcService.calcDefendEffect(matchData);



            System.out.printf("\t[calculateTurn @ %s] AFTER SWITCH ATTACKER IDX: %d, DEFENDER IDX: %d\n", matchData.getId(), attackerIdx, defenceIdx);

            
            isPlayerKO = false;
        }
        // END DAMAGE CALCULATION LOGIC --------------------------

        // 다시 turn을 시작할 준비를 합니다.
        // Update player datas
        for (PlayerData player : players) {
            player.setSelecting(false);
            // player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            player.setAckState(ACK_TYPE.NO_ACK);
        }
        attackerPlayer.setAttacking(true);
        defencePlayer.setAttacking(false);
        matchData.setAttackerPlayerIdx(attackerIdx);

        // Update match data.
        matchData.setCurrentTurn(matchData.getCurrentTurn() + 1);
        matchData.setState(MATCH_STATE.GAME_TURN_ANIMATION);

        // (FIXME) End game as soon as player downs another.
        if (isPlayerKO) {
            attackerPlayer.setWins(attackerPlayer.getWins() + 1);
            matchData.setKo(true);

            int winnerPlayerIdx = -1;
            int winsMax = -1;

            for (int i = 0; i < players.size(); i++) {
                PlayerData player = players.get(i);

                if (player.getWins() > winsMax) {
                    winnerPlayerIdx = i;
                    winsMax = player.getWins();
                }
            }

            if (winnerPlayerIdx != -1) {
                players.get(winnerPlayerIdx).setFinalWinner(true);
            }

            matchData.setWinnerPlayerIdx(winnerPlayerIdx);
            // TODO: 라운드를 다른 곳에서 바꿔야 할 수 도..
            matchData.setCurrentRound(matchData.getCurrentRound() + 1);
            matchData.setCurrentTurn(0);
//            matchData.setState(MATCH_STATE.GAME_ROUND_END_PLAYER_KO);
        }
    }

    private void applyPerksInAttackSuccessDecision(MatchData matchData, int defenderIdx) {
        PlayerData defender = matchData.getPlayers().get(defenderIdx);
        if (defender.getPerkList() == null) {
            return;
        }

        for (PERK perkEnum : defender.getPerkList()) {
            if (perkEnum != PERK.FLEXIBLE_HANDS && perkEnum != PERK.FLEXIBLE_MIND) {
                continue;
            }

            IPerk perk = PerkFactory.createPerk(perkEnum);
            perk.usePerkIfPossible(matchData, defenderIdx);
        }
    }
}
