package team2.mse.ajou.server.domain.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

import java.util.List;
import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
@Service
public class ItemService implements IItemService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final FrdbService frdbService;

    @Autowired
    public ItemService(PlayerDataRepository playerDataRepository,
                           MatchDataRepository matchDataRepository,
                           MatchTurnCalcService matchTurnCalcService,
                           MatchService matchService,
                           FrdbService frdbService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.frdbService = frdbService;
    }

    public void receiveItemAnimationEndAck(UUID id) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION);

        // 둘다 ack 받으면 round 시작
        if(isAllItemAnimationEnd(matchData)){
            matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);
            MatchData updMatchData = matchDataRepository.save(matchData);
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }

    private boolean isAllItemAnimationEnd(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        if(players.size()!=2) return false;
        for(PlayerData player : players) {
            if(player.getAckState() != ACK_TYPE.TURN_ANIMATION_END) return false;
        }
        return true;
    }
}
