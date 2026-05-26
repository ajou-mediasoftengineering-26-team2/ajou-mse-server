package team2.mse.ajou.server.domain.ack.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * @author Junseo Hwang 202322128
 */
@Service
public class AckService {

    /**
     * match의 플레이어들이 ackType의 ack를 모두 보냈는지 확인합니다.
     * @param matchData
     * @param ackType
     * @return
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
