package team2.mse.ajou.server.domain.perk.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;

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
    private final GameDataRepository gameDataRepository;

    public PerkService(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public void putPerkChoice(UUID id, PERK perk) {
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
        gameDataRepository.findPlayerById(id).ifPresentOrElse(playerData -> {
            if (playerData.getJoinedMatchId() != null) {
                gameDataRepository.findMatchById(playerData.getJoinedMatchId()).ifPresentOrElse(matchData -> {
                    if (matchData.getState() != MATCH_STATE.GAME_PERK_ITEM_RECEIVING) {
                        throw new IllegalStateException("Perk/item receive animation ACK can be submitted only after turn result is calculated!");
                    }
                }, () -> {
                    throw new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId());
                });
            } else {
                throw new IllegalStateException("Player is not in match!");
            }

            if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
                throw new IllegalStateException("Player is already acknowledged!");
            }

            playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
            gameDataRepository.savePlayer(playerData);
        }, () -> {
            throw new IllegalArgumentException("Player not Found: " + id);
        });

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
