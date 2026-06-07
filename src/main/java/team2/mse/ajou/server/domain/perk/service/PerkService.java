package team2.mse.ajou.server.domain.perk.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;

import java.util.*;

/**
 * Perk data handling service
 *
 * @author Junseo Hwang 202322128
 */
@Service
public class PerkService implements IPerkService {
    private final GameDataRepository gameDataRepository;

    public PerkService(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public void putPerkChoice(UUID id, PERK perk) {
        var playerData = gameDataRepository.findPlayerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player Not Found: " + id));
        UUID matchId = playerData.getJoinedMatchId();
        var matchData = gameDataRepository.findMatchById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + matchId));

        if (matchData.getState() != MATCH_STATE.GAME_PERK_ITEM_RECEIVING) {
            throw new IllegalStateException("Perk/item receive animation ACK can be submitted only after turn result is calculated!");
        }

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        playerData.setPerkChoiceCurrent(perk);
        playerData.getPerkList().add(perk);
        
        // matchData.updatePlayer(playerData);
        // gameDataRepository.saveMatch(matchData);
        gameDataRepository.savePlayer(playerData);

        System.out.printf("[PLR] putPerkChoice(PLR: %s, PERK: %s) -> %s\n", id, perk, playerData.getPerkList());
        // gameDataRepository.updateFrdbMatchData(matchData);
        // gameDataRepository.savePlayer(playerData);

        /*
        PlayerData playerData = playerDataJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setPerkChoiceCurrent(perk);
        playerData.getPerkList().add(perk);

        matchData.updatePlayer(playerData);

        playerDataJpaRepository.save(playerData);
        matchDataJPARepository.save(matchData);

        // MatchData updMatchData = matchDataRepository.save(matchData);
        // frdbService.setMatch(updMatchData.getId(), updMatchData);
         */
    }

    @Override
    public void putAck(UUID id) {
        var playerData = gameDataRepository.findPlayerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player Not Found: " + id));
        UUID matchId = playerData.getJoinedMatchId();
        var matchData = gameDataRepository.findMatchById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + matchId));

        if (matchData.getState() != MATCH_STATE.GAME_PERK_ITEM_RECEIVING) {
            throw new IllegalStateException("Perk/item receive animation ACK can be submitted only after turn result is calculated!");
        }

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
        gameDataRepository.savePlayer(playerData);

        /*
        PlayerData playerData = playerDataJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
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

        playerDataJpaRepository.save(playerData);
        MatchData updMatchData = matchDataJPARepository.save(matchData);
        frdbRepository.setMatch(updMatchData.getId(), updMatchData);
         */
    }

    @Override
    public void giveRandomPerkChoiceList(MatchData matchData) {
        // -> 1] 선택 가능한 모든 perk 목록 불러오기
        // -> 2] 그 중 (최대) 3개의 랜덤한 것을 전달하기
        List<PlayerData> players = matchData.getPlayers();
        for (PlayerData player : players) {
            List<PERK> availablePerks = new ArrayList<>(getUnownedPerks(player));
            int returnSz = Math.min(availablePerks.size(), 3);

            Collections.shuffle(availablePerks);
            player.setPerkChoiceCurrent(null);
            player.setPerkChoiceList(availablePerks.subList(0, returnSz));
        }
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
