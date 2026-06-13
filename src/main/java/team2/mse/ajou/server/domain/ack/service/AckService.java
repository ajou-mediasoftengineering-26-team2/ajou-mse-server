package team2.mse.ajou.server.domain.ack.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * Provides utilities related to ACK.
 * ack와 관련된 유틸을 제공해줌
 * @author Junseo Hwang 202322128
 */
@Service
public class AckService {

    /**
     * match의 플레이어들이 ackType의 ack를 모두 보냈는지 확인합니다.
     * This checks whether all players have sent an ACK.
     * @param matchData Match you want to check
     * @param ackType ack type you want to check
     * @return if all player hve sent ack return true, otherwise false.
     */
    public boolean isAllAckReceived(MatchData matchData, ACK_TYPE ackType) {
        List<PlayerData> players = matchData.getPlayers();
        if(players.size()!=2) return false;

        for(PlayerData player : players) {
            if(player.getAckState() != ackType) return false;
        }
        return true;
    }
}
