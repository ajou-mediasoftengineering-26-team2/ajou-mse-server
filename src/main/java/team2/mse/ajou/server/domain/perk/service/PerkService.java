package team2.mse.ajou.server.domain.perk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Perk data handling service
 *
 * @author Junseo Hwang 202322128
 */
@Service
public class PerkService implements IPerkService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final MatchService matchService;
    private final FrdbService frdbService;
    private final AckService ackService;

    @Autowired
    public PerkService(PlayerDataRepository playerDataRepository,
                       MatchDataRepository matchDataRepository,
                       MatchService matchService,
                       FrdbService frdbService, AckService ackService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.matchService = matchService;
        this.frdbService = frdbService;
        this.ackService = ackService;
    }

    @Override
    public void putPerkChoice(UUID id, PERK perk) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setPerkChoiceCurrent(perk);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        // MatchData updMatchData = matchDataRepository.save(matchData);
        // frdbService.setMatch(updMatchData.getId(), updMatchData);
    }

    @Override
    public void putAck(UUID id) {
        PlayerData playerData = playerDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        if (matchData.getState() != MATCH_STATE.GAME_PERK_ITEM_RECEIVING) {
            throw new IllegalStateException("Perk/item receive animation ACK can be submitted only after turn result is calculated!");
        }

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        // 둘 다 ACK 받으면 다음 라운드로 이동
        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ITEM_RECEIVE_ANIMATION_END)) {
            // (라운드 리셋 + state 설정)
            matchService.initializeMatchRound(matchData);
        }

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);
    }

    /**
     * Calculates perks that are not owned by given `PlayerData`, available for selection.
     *
     * @param playerData Player data.
     * @return List
     * @author Ahn Yubin / 202021088
     */
    @Override
    public List<PERK> getUnownedPerks(PlayerData playerData) {
        return Arrays.stream(PERK.values())
                .filter(perk -> !playerData.getPerkList().contains(perk))
                .toList();
    }
}
