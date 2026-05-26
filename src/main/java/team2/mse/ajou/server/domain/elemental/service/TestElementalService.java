package team2.mse.ajou.server.domain.elemental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

import java.util.UUID;

@Service("TestElementalService")
public class TestElementalService implements IElementalService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final FrdbService frdbService;
    private final AckService ackService;

    @Autowired
    public TestElementalService(PlayerDataRepository playerDataRepository,
                           MatchDataRepository matchDataRepository,
                           MatchTurnCalcService matchTurnCalcService,
                           MatchService matchService,
                           FrdbService frdbService,
                           AckService ackService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.frdbService = frdbService;
        this.ackService = ackService;
    }

    @Override
    public void putElementalChoice(UUID id, HAND_ELEMENTAL handElemental) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        // TEST임
        playerData.setAckState(ACK_TYPE.__TEST_ACK);

        playerData.setHandElemental(handElemental);
        matchData.updatePlayer(playerData);

        // TEST임
        if(ackService.isAllAckReceived(matchData, ACK_TYPE.__TEST_ACK)){
            matchData.setState(MATCH_STATE.GAME_ELEMENTAL_RECEIVING);
        }

        playerDataRepository.save(playerData);
        MatchData updMatchData = matchDataRepository.save(matchData);

        frdbService.setMatch(updMatchData.getId(), updMatchData);
    }

    @Override
    public void receiveElementalAnimationEndAck(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        if(ackService.isAllAckReceived(matchData, ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END)){
            matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);
            MatchData updMatchData = matchDataRepository.save(matchData);
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }

}
