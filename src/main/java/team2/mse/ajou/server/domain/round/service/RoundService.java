package team2.mse.ajou.server.domain.round.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

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

    public RoundService(AckService ackService,
                        PlayerDataRepository playerDataRepository,
                        MatchDataRepository matchDataRepository,
                        MatchService matchService,
                        FrdbService frdbService) {
        this.ackService = ackService;
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.frdbService = frdbService;
    }

    @Override
    public void receiveRoundStart(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ROUND_START_ANIMATION_END);

        // 라운드 시작 애니메이션 종료 -> 플레이어 공격 선택
        if(ackService.isAllAckReceived(matchData, ACK_TYPE.ROUND_START_ANIMATION_END)){
            matchData.setState(MATCH_STATE.GAME_PLAYER_CHOICE);
            MatchData updMatchData = matchDataRepository.save(matchData);
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }

    @Override
    public void receiveRoundEnd(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ROUND_END_ANIMATION_END);

        // 라운드 종료 애니메이션 종료(플레이어 ko)
        // -> 1라운드면 elemental 선택
        // -> 2라운드 이상이면 perk 선택
        if(ackService.isAllAckReceived(matchData, ACK_TYPE.ROUND_END_ANIMATION_END)){
            if(matchData.getCurrentRound() == 1){
                matchData.setState(MATCH_STATE.GAME_ELEMENTAL_CHOICE);
            }
            else{
                matchData.setState(MATCH_STATE.GAME_PERK_CHOICE);
            }
            MatchData updMatchData = matchDataRepository.save(matchData);
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }
}
