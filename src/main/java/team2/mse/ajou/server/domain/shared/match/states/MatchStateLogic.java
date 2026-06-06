package team2.mse.ajou.server.domain.shared.match.states;

import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.service.RunningMatch;

import java.util.UUID;

/**
 * 매치 State: State별 동작 정의 인터페이스.
 * Match State: Interface for logic to run for each states.
 *
 * @author Ahn Yubin / 202021088
 */
public interface MatchStateLogic {
    /**
     * FRDB 등 올릴 때 사용할 상태 코드 String 값.
     * State code for use in FRDB etc.
     *
     * @return 상태 코드값.
     */
    String getSerializedName();

    /**
     * 본 상태가 인게임 여부 (i.e. 새로운 플레이어가 참여 가능한지 여부)
     * Whether if this state is considered ingame (i.e. may new players join)
     *
     * @return 인게임 여부.
     */
    boolean getIsIngame();

    // State별 플레이어 입력 등 콜백
    void onPlayerJoin(RunningMatch context, UUID playerId);

    void onPlayerLeave(RunningMatch context, UUID playerId);

    void onPlayerAck(RunningMatch context, UUID playerId, ACK_TYPE type);

    void onEnter(RunningMatch context);

    void onExit(RunningMatch context);
}
