package team2.mse.ajou.server.domain.round.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.item.service.IItemService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;

import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
@Service
public class RoundService implements IRoundService {
    private final AckService ackService;
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final FrdbService frdbService;
    private final MatchService matchService;
    private final IItemService itemService;

    public RoundService(AckService ackService,
                        PlayerDataRepository playerDataRepository,
                        MatchDataRepository matchDataRepository,
                        MatchService matchService,
                        FrdbService frdbService, IItemService itemService) {
        this.ackService = ackService;
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.frdbService = frdbService;
        this.matchService = matchService;
        this.itemService = itemService;
    }

    @Override
    public void receiveRoundStart(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ROUND_START_ANIMATION_END);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        System.out.println(playerId + ": round start-ack");

        // 라운드 시작 애니메이션 종료 -> 플레이어 공격 선택
        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ROUND_START_ANIMATION_END)) {
            matchData.setState(MATCH_STATE.GAME_PLAYER_CHOICE);
            MatchData updMatchData = matchDataRepository.save(matchData);

            frdbService.setMatch(updMatchData.getId(), updMatchData);

            matchService.startNextTurn(matchData.getId());
        }
    }

    @Override
    public void receiveRoundEnd(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ROUND_END_ANIMATION_END);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        System.out.println(playerId + ": round end-ack");

        // 라운드 종료 애니메이션 종료(플레이어 ko)
        // -> 1라운드면 elemental 선택
        // -> 2라운드 이상이면 perk 선택
        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ROUND_END_ANIMATION_END)) {
            boolean isElementalChoice = matchData.getCurrentRound() == 1;

            if (isElementalChoice) {
                matchData.setState(MATCH_STATE.GAME_ELEMENTAL_CHOICE);
            } else {
                matchData.setState(MATCH_STATE.GAME_PERK_CHOICE);
            }

            // 10초가 지나면 서버는 perk item receiving / elemental receiving 상태가 되도록 타이머 ON
            MATCH_STATE nextState = isElementalChoice ? MATCH_STATE.GAME_ELEMENTAL_RECEIVING : MATCH_STATE.GAME_PERK_ITEM_RECEIVING;

            matchService.setCountdownForMatch(matchData, () -> {
                // "이때 perk(elemental)과 item이 다 업데이트 됨"
                itemService.giveRandomItem(matchData.getId());
                // (이미 perk는 perkservice에서 갱신됨)

                // "클라이언트는 perk, item 수령 애니메이션을 출력하고 ack를 보내면 됨"
                matchData.setState(nextState);

                MatchData updMatchData = matchDataRepository.save(matchData);
                frdbService.setMatch(updMatchData.getId(), updMatchData);
            }, 10);

            MatchData updMatchData = matchDataRepository.save(matchData);
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }
}
