package team2.mse.ajou.server.domain.perk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;

import java.util.UUID;

/**
 * Handle perk system
 *
 * @author Junseo Hwang 202322128
 */
@Service("TestPerkService")
public class TestPerkService implements IPerkService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final FrdbService frdbService;
    private final AckService ackService;
    private final MatchService matchService;

    @Autowired
    public TestPerkService(PlayerDataRepository playerDataRepository,
                           MatchDataRepository matchDataRepository,
                           MatchService matchService,
                           FrdbService frdbService,
                           AckService ackService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.frdbService = frdbService;
        this.ackService = ackService;
        this.matchService = matchService;
    }

    @Override
    public void putPerkChoice(UUID id, PERK perk) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.getPerkList().add(perk);
        playerData.setPerkList(playerData.getPerkList());
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        MatchData updMatchData = matchDataRepository.save(matchData);

        frdbService.setMatch(updMatchData.getId(), updMatchData);
    }

    @Override
    public void putAck(UUID id) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        // 둘 다 ACK 받으면 다음 라운드로 이동
        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ITEM_RECEIVE_ANIMATION_END)) {
            // (라운드 리셋 + state 설정)
            matchService.initializeMatchRound(matchData);
        }

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);
    }
}
