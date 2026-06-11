package team2.mse.ajou.server.domain.subway.model.stations;

import org.springframework.context.annotation.Bean;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Station - 시청
 * 매 라운드마다 특정 행동이 금지됨
 * A specific hand behavior is forbidden every round.
 * @author Junseo Hwang 202322128
 */
public class StationCityHall implements IStation {
    @Override
    public void applyIfPossible(MatchData matchData) {
        if(isAvailable(matchData)) {
            MATCH_STATE state = matchData.getState();

            // 손 선택 후, 금지된 행동을 고른 사람의 행동을 forbidden behavior로 변경
            // After hand selection, change the action of the player who selected the forbidden action to FORBIDDEN_BEHAVIOR.
            if(state == MATCH_STATE.GAME_CHOICE_FINISHED) {
                List<PlayerData> players = matchData.getPlayers();
                for(PlayerData player : players) {
                    if(player.getChoice() == matchData.getForbiddenBehavior()){
                        player.setChoice(HAND_CHOICE.FORBIDDEN_BEHAVIOR);
                    }
                }
            }

            // 랜덤한 행동을 금지 행동으로 정함
            // Randomly selects an action as the forbidden behavior.
            else{
                List<HAND_CHOICE> handChoices = new ArrayList<>(
                        Arrays.stream(HAND_CHOICE.values())
                                .filter(handChoice -> handChoice != HAND_CHOICE.FORBIDDEN_BEHAVIOR)
                                .toList());
                Collections.shuffle(handChoices);

                matchData.setForbiddenBehavior(handChoices.getFirst());
            }
        }
    }

    @Override
    public boolean isAvailable(MatchData matchData) {
        MATCH_STATE state = matchData.getState();
        return state == MATCH_STATE.LOBBY_START_COUNTDOWN
                || state == MATCH_STATE.GAME_ROUND_START_ANIMATION
                || state == MATCH_STATE.GAME_ELEMENTAL_RECEIVING
                || state == MATCH_STATE.GAME_PERK_ITEM_RECEIVING
                || state == MATCH_STATE.GAME_CHOICE_FINISHED;
    }
}
