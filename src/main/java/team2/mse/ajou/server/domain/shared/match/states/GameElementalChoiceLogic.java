package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

/**
 * 플레이어가 hand elemental 선택중
 *
 * @author Ahn Yubin / 202021088
 */
public class GameElementalChoiceLogic implements IMatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        context.setTimerAndRun(10, () -> {
            System.out.printf("\t[STATE] GameElementalChoiceLogic::setTimerAndRun(MATCH: %s) | ELEMENTAL RECEIVING TIMER END!\n", context.getMatchId());

            context.getMatchData(context.getMatchId()).ifPresentOrElse(countdownMatchData -> {
                // (perk: perkChoiceCurrent값에 해당하는 perk 지급 & perkChoiceList 빈 리스트로 갱신)
                // ACK 할 수 있도록 셋팅
                countdownMatchData.clearAck();

                // "클라이언트는 perk, item 수령 애니메이션을 출력하고 ack를 보내면 됨"
                countdownMatchData.setState(MATCH_STATE.GAME_ELEMENTAL_RECEIVING);

                context.commitMatchData(countdownMatchData);
                context.commitFrdbData(countdownMatchData);
            }, () -> {
                System.err.printf("\t[STATE] GameElementalChoiceLogic::setTimerAndRun(MATCH: %s) | MATCH NOT FOUND IN DB!\n", context.getMatchId());
            });
        });
    }
}
