package team2.mse.ajou.server.domain.elemental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

import java.util.UUID;

/**
 * Elemental Service
 *
 * @author Junseo Hwang 202322128
 * @author Ahn Yubin 202021088
 */
@Service
public class ElementalService implements IElementalService {
    private final int[] costOfUpgrade = {0, 10, 20, 30, 100, 9999};

    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final MatchService matchService;
    private final FrdbService frdbService;
    private final AckService ackService;

    @Autowired
    public ElementalService(PlayerDataRepository playerDataRepository,
                            MatchDataRepository matchDataRepository,
                            MatchTurnCalcService matchTurnCalcService,
                            MatchService matchService,
                            FrdbService frdbService,
                            AckService ackService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.matchService = matchService;
        this.frdbService = frdbService;
        this.ackService = ackService;
    }

    @Override
    public void putElementalChoice(UUID id, HAND_ELEMENTAL handElemental) {
        /*
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        // TEST임
        playerData.setAckState(ACK_TYPE.__TEST_ACK);

        playerData.setHandElemental(handElemental);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        // TEST임
        if(ackService.isAllAckReceived(matchData, ACK_TYPE.__TEST_ACK)){
            matchData.setState(MATCH_STATE.GAME_ELEMENTAL_RECEIVING);
        }

        playerDataRepository.save(playerData);
        MatchData updMatchData = matchDataRepository.save(matchData);

        frdbService.setMatch(updMatchData.getId(), updMatchData);
         */

        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setHandElemental(handElemental);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        MatchData updMatchData = matchDataRepository.save(matchData);
        frdbService.setMatch(updMatchData.getId(), updMatchData);
    }

    @Override
    public void upgradeElemental(UUID id, HAND_ELEMENTAL handElemental) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        // 업그레이드 못하는데 업그레이드 쿼리가 들어온 경우 (he is hacker!!)
        if(playerData.getCoin() < playerData.getUpgradeCost()) {
            throw new IllegalArgumentException("Coin is less than Cost: " + id);
        }

        if(playerData.getHandElemental() == HAND_ELEMENTAL.NONE) {
            return;
        }

        playerData.setCoin(playerData.getCoin() - playerData.getUpgradeCost());
        playerData.setElementalLevel(playerData.getElementalLevel() + 1);
        playerData.setUpgradeCost(costOfUpgrade[playerData.getElementalLevel()]);

        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        MatchData updMatchData = matchDataRepository.save(matchData);
        frdbService.setMatch(updMatchData.getId(), updMatchData);
    }

    @Override
    public void receiveElementalAnimationEndAck(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        System.out.println(playerId + ": elemental receiving animation end-ack");

        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END)) {
            System.out.println(matchData.getId() + ": round start!");
            // (라운드 리셋 + state 설정)
            matchService.initializeMatchRound(matchData);
        }

        playerDataRepository.save(playerData);
        MatchData updMatchData = matchDataRepository.save(matchData);
        frdbService.setMatch(updMatchData.getId(), updMatchData);
    }

}
