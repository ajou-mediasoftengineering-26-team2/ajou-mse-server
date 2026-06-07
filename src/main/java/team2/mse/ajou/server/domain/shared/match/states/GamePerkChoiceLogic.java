package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

/**
 * 플레이어가 perk 선택 중
 *
 * @author Ahn Yubin / 202021088
 */
public class GamePerkChoiceLogic implements MatchStateLogic {
    @Override
    public void onEnter(RunningMatch context) {
        context.setTimerAndRun(10, () -> {
            System.out.printf("\t[STATE] GamePerkChoiceLogic::setTimerAndRun(MATCH: %s) | PERK RECEIVING TIMER END!\n", context.getMatchId());

            context.getMatchData(context.getMatchId()).ifPresentOrElse(countdownMatchData -> {
                // "이때 perk(elemental)과 item이 다 업데이트 됨"
                // (item: 랜덤 아이템 지급)
                context.updateMatchDataForItemReceiving(countdownMatchData);

                // (perk: perkChoiceCurrent값에 해당하는 perk 지급 & perkChoiceList 빈 리스트로 갱신)
                for (PlayerData player : countdownMatchData.getPlayers()) {
                    // 여기서 perk을 바꾸면 안됩니다!!!
                    // 기본적으로 perk을 null로 설정하기 때문에
                    // perk을 선택하지 않는 elemental에서 null이 list에 들어가버립니다!!!!!!!!!!!!
                    // ACK 할 수 있도록 셋팅
                    player.setAckState(ACK_TYPE.NO_ACK);
                }

                // "클라이언트는 perk, item 수령 애니메이션을 출력하고 ack를 보내면 됨"
                countdownMatchData.setState(MATCH_STATE.GAME_PERK_ITEM_RECEIVING);

                context.commitMatchData(countdownMatchData);
                context.commitFrdbData(countdownMatchData);
            }, () -> {
                System.err.printf("\t[STATE] GamePerkChoiceLogic::setTimerAndRun(MATCH: %s) | MATCH NOT FOUND IN DB!\n", context.getMatchId());
            });
        });
    }
}
