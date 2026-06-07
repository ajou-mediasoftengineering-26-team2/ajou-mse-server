package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.Collections;
import java.util.List;

/**
 * 인게임: 한 라운드 끝. 플레이어 사망
 *
 * @author Ahn Yubin / 202021088
 */
public class GameRoundEndPlayerKoLogic implements MatchStateLogic {
    @Override
    public void onMatchPlayerAckStateUpdate(RunningMatch context, List<ACK_TYPE> ackState) {
        var condition = ackState.size() >= 2 && ackState.stream().allMatch(ack -> ack == ACK_TYPE.ROUND_END_ANIMATION_END);
        System.out.printf("\t[STATE] GameRoundEndPlayerKoLogic::onMatchPlayerAckStateUpdate(MATCH: %s) - %s\n", context.getMatchId(), ackState);

        // 라운드 종료 애니메이션 종료(플레이어 ko)
        // -> 1라운드면 elemental 선택
        // -> 2라운드 이상이면 perk 선택
        if (condition) {
            var matchData = context.getMatchData(context.getMatchId()).orElse(null);

            if (matchData == null) {
                return;
            }

            System.out.printf("\t[STATE] GameRoundEndPlayerKoLogic::onMatchPlayerAckStateUpdate(MATCH: %s) | ALL ROUND_END_ANIMATION_END ACK RECEIVED\n", context.getMatchId());

            boolean isElementalChoice = matchData.getCurrentRound() == 1;

            if (isElementalChoice) {
                matchData.setState(MATCH_STATE.GAME_ELEMENTAL_CHOICE);
            } else {
                // perk 선택의 경우, 두 플레이어 모두 선택지 리스트 생성
                matchData.setState(MATCH_STATE.GAME_PERK_CHOICE);
                var players = matchData.getPlayers();

                // -> 1] 선택 가능한 모든 perk 목록 불러오기
                // -> 2] 그 중 (최대) 3개의 랜덤한 것을 전달하기
                for (PlayerData player : players) {
                    var availablePerks = context.getAvailablePerks(player.getPerkList());
                    int returnSz = Math.min(availablePerks.size(), 3);

                    Collections.shuffle(availablePerks);
                    player.setPerkChoiceCurrent(null);
                    player.setPerkChoiceList(availablePerks.subList(0, returnSz));
                }
            }

            // 10초가 지나면 서버는 perk item receiving / elemental receiving 상태가 되도록 타이머 ON
            // 실제 타이머는 각각 `GamePerkChoiceLogic`, `GameElementalChoiceLogic`에서 실행되니 참고부탁...
            context.commitMatchData(matchData);
            context.commitFrdbData(matchData);
        }
    }
}
