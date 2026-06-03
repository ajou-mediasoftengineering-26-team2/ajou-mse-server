package team2.mse.ajou.server.domain.turn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
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
 * Update the DB on each turn
 * Save the player's selection to the DB.
 *
 * @author Junseo Hwang 202322128
 */
@Service
public class TurnService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final MatchTurnCalcService matchTurnCalcService;
    private final MatchService matchService;
    private final FrdbService frdbService;

    @Autowired
    public TurnService(PlayerDataRepository playerDataRepository,
                       MatchDataRepository matchDataRepository,
                       MatchTurnCalcService matchTurnCalcService,
                       MatchService matchService,
                       FrdbService frdbService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.matchTurnCalcService = matchTurnCalcService;
        this.matchService = matchService;
        this.frdbService = frdbService;
    }
    /**
     * 플레이어의 선택을 playerDatabase에 저장
     * 아마도 choice가 시작된지 5초 후에 실행될 겁니다.
     * 두 플레이어에게 모두 받으면 데미지를 계산, firebase update
     *
     * @param id player's uuid
     * @param choice hand action which player choose
     * @throws Exception
     */
    public void putPlayerInput(String id, String choice) throws Exception {
        UUID uuid = UUID.fromString(id);
        PlayerData playerData = playerDataRepository.findById(uuid)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));
        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        if (matchData.getState() != MATCH_STATE.GAME_CHOICE_FINISHED) {
            throw new IllegalStateException("Choice can be submitted only after the 5-second timer is finished!");
        }
        // When a request is received while it is not the player’s turn.
        if (!playerData.isSelecting()) {
            throw new IllegalStateException("Player is not selecting!");
        }

        HAND_CHOICE handChoice = HAND_CHOICE.valueOf(choice);
        System.out.printf("\t[%s] PLAYER [`%s`] INPUT: (%s) `%s`\n", matchData.getId(), uuid, handChoice, choice);

        playerData.setChoice(handChoice);
        playerData.setSelecting(false);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        if (isAllChoiceSubmitted(matchData)) {
            System.out.printf("[%s] TURN CONTINUE\n", matchData.getId());
            matchTurnCalcService.calculateTurn(matchData);

            playerDataRepository.saveAll(matchData.getPlayers());
            MatchData updMatchData = matchDataRepository.save(matchData);

            // Clients should observe damageList / hp / isAttackSuccess and play result animation.
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }


    /**
     * 플레이어의 ack를 받습니다.
     * 두 플레이어에게 모두 받으면 다음 턴 실행
     *  TODO: KO 구분을 해야합니다.
     * @param id player's uuid
     * @throws Exception
     */
    public void receiveTurnAnimationEndAck(String id) throws Exception {
        UUID uuid = UUID.fromString(id);
        PlayerData playerData = playerDataRepository.findById(uuid)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        if (matchData.getState() != MATCH_STATE.GAME_TURN_ANIMATION) {
            throw new IllegalStateException("Turn animation ACK can be submitted only after turn result is calculated!");
        }

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        playerData.setAckState(ACK_TYPE.TURN_ANIMATION_END);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        if (isAllTurnAnimationEnd(matchData)) {
            if(matchData.isKo()){
                //일단 스테이트만 넘겨봐
                matchData.setState(MATCH_STATE.GAME_ROUND_END_PLAYER_KO);
                MatchData updMatchData = matchDataRepository.save(matchData);
                frdbService.setMatch(updMatchData.getId(), updMatchData);
            }
            else{
                matchService.startNextTurn(matchData.getId());
            }
        }
    }

    private boolean isAllChoiceSubmitted(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        if(players.size()!=2) return false;
        for(PlayerData player : players) {
            if(player.isSelecting()) return false;
        }
        return true;
    }

    private boolean isAllTurnAnimationEnd(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        if(players.size()!=2) return false;
        for(PlayerData player : players) {
            if(player.getAckState() != ACK_TYPE.TURN_ANIMATION_END) return false;
        }
        return true;
    }
}
