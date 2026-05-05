package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * 매치: 턴/게임 로직 계산을 하청받는 서비스.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class MatchTurnCalcService {
    /**
     * 주어진 MatchData를 초기화시킵니다.
     * MatchData 안에는 플레이어 정보, 턴 수 등 필요한 정보가 모두 들어가있기에 파라미터를 한 개만 받아도 됩니다.
     * 턴을 실행하면 해당 객체가 수정됩니다. 이후 DB에 저장하거나 FRDB 갱신에 사용 가능하겠죠?
     * @param matchData 현재 매치 정보 레퍼런스.
     */
    public void initializeMatch(MatchData matchData) {
        int playerIdx = matchData.getCurrentPlayerIdx();
        int attackerIdx = matchData.getAttackerPlayerIdx();

        List<PlayerData> players = matchData.getPlayers();
        // System.out.println("Players: " + players);
        if (players.size() < 2) {
            throw new ApiError(5005, "Insufficient players in the match!");
        }

        // 공격수 플레이어
        PlayerData attackerPlayer = players.get(attackerIdx);
        attackerPlayer.setAttacking(true);
        attackerPlayer.setSelecting(true);

        // 수비수 플레이어
        PlayerData defencePlayer = players.get((attackerIdx + 1) % players.size());
        defencePlayer.setAttacking(false);
        defencePlayer.setSelecting(false);

        // 모든 플레이어 가만히로 자동선택 & HP 최대치로 etc
        for (PlayerData player : players) {
            player.setHp(10);
            player.setWins(0);
            player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
        }

        // 첫 턴: 공격수부터 입력 받기
        matchData.setState(MATCH_STATE.GAME_ATK_CHOICE);
    }

    /**
     * 주어진 MatchData를 가지고 한 턴을 실행합니다.
     * MatchData 안에는 플레이어 정보, 턴 수 등 필요한 정보가 모두 들어가있기에 파라미터를 한 개만 받아도 됩니다.
     * 턴을 실행하면 해당 객체가 수정됩니다. 이후 DB에 저장하거나 FRDB 갱신에 사용 가능하겠죠?
     * @param matchData 현재 매치 정보 레퍼런스.
     */
    public void calculateTurn(MatchData matchData) {
        MATCH_STATE state = matchData.getState();
        List<PlayerData> players = matchData.getPlayers();

        int playerIdx = matchData.getCurrentPlayerIdx();
        int attackerIdx = matchData.getAttackerPlayerIdx();
        int defenceIdx = (attackerIdx + 1) % players.size();

        if (players.size() < 2) {
            throw new ApiError(5005, "Insufficient players in the match!");
        }

        // 공격수 플레이어
        PlayerData attackerPlayer = players.get(attackerIdx);
        HAND_CHOICE attackerChoice = attackerPlayer.getChoice();
        // 수비수 플레이어
        PlayerData defencePlayer = players.get(defenceIdx);
        HAND_CHOICE defenceChoice = defencePlayer.getChoice();

        // 공격 성공 여부
        boolean isAttackSuccess = false;
        // 이번 턴에 공격수가 플레이어를 죽였는지 여부
        boolean isPlayerKO = false;

        // 여기부터 데미지 등 계산 로직 시작 --------------------------
        switch (state) {
            // GAME_ATK_CHOICE -> GAME_DEF_CHOICE 전환
            case GAME_ATK_CHOICE:
                // 공격 제한시간 끝
                // 수비수 차례로 넘기기...
                matchData.setState(MATCH_STATE.GAME_DEF_CHOICE);
                break;
            // GAME_DEF_CHOICE -> GAME_ATK_CHOICE 전환
            case GAME_DEF_CHOICE:
                // 방어 제한시간 끝. 누가 누가 잘 했는지 볼까요...
                // 현재 기준 공격 성공 여부는 "공격수의 choice와 수비수의 choice가 불일치하는지" 입니다.
                isAttackSuccess = attackerChoice != defenceChoice;

                // TODO: 피격시 효과 등 더 자세한 로직 구현
                if (isAttackSuccess) {
                    // 공격에 성공했나..? 그러면 수비수 플레이어를 뚜까팹시다.
                    // FIXME: 데미지는 현재 상수 (2)입니다. 만약 공격수 손동작에 따라 데미지를 바꿀려면 `attackerChoice` 값을 참고해서 여기서 계산하면 될 것 같아요
                    int damageAmount = 2;

                    defencePlayer.setHp(Math.max(0, defencePlayer.getHp() - damageAmount));
                    isPlayerKO = (defencePlayer.getHp() <= 0);
                } else {
                    // 허거덩스 방어성공. 역할을 바꿔요
                    attackerIdx = (attackerIdx + players.size() + 1) % players.size();
                    isPlayerKO = false;
                }

                // 공격수 차례로 넘기기 + 다음 턴...
                matchData.setState(MATCH_STATE.GAME_ATK_CHOICE);
                matchData.setCurrentTurn(matchData.getCurrentTurn() + 1);
                break;
        }

        // 그리고 해치웠나..? 그러면 공격수 플레이어에게 1킬 추가
        if (isPlayerKO) {
            attackerPlayer.setWins(attackerPlayer.getWins() + 1);
            matchData.setState(MATCH_STATE.GAME_ROUND_END_PLAYER_KO);
        }

        // 차례에 알맞게 다음 선택할 플레이어 인덱스 계산
        if (matchData.getState() == MATCH_STATE.GAME_ATK_CHOICE) {
            playerIdx = attackerIdx;
        } else {
            playerIdx = defenceIdx;
        }
        // 여기까지 데미지 등 계산 로직 끝 --------------------------

        // 매치 정보 갱신
        matchData.setAttackSuccess(isAttackSuccess);
        matchData.setCurrentPlayerIdx(playerIdx);
        matchData.setAttackerPlayerIdx(attackerIdx);

        // 플레이어 정보 갱신
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);

            // 선택 여부
            if (i == playerIdx) {
                System.out.println("PLAYER " + player.getUsername() + " SELECTS!");
                player.setSelecting(true);

                // 선택 리셋 -> "가만히"로 내부 상태 자동 리셋
                player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            } else {
                player.setSelecting(false);
            }

            // 공격수 여부
            if (i == attackerIdx) {
                System.out.println("PLAYER " + player.getUsername() + " ATTACKS!");
                player.setAttacking(true);
            } else {
                player.setAttacking(false);
            }
        }

        // (FIXME) 우선은 게임 끝...
        if (isPlayerKO) {
            int winnerPlayerIdx = -1,
                winsMax = -1;

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

            matchData.setCurrentRound(matchData.getCurrentRound() + 1);
            matchData.setCurrentTurn(0);
            matchData.setState(MATCH_STATE.END_RESULT);
        }
    }
}
