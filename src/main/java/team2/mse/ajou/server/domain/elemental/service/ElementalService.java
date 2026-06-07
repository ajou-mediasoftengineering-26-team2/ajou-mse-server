package team2.mse.ajou.server.domain.elemental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbRepository;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchServiceLegacy;
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

    private final GameDataRepository gameDataRepository;

    public ElementalService(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public void putElementalChoice(UUID id, HAND_ELEMENTAL handElemental) {
        gameDataRepository.findPlayerById(id).ifPresentOrElse(playerData -> {
            playerData.setHandElemental(handElemental);
            gameDataRepository.savePlayer(playerData);
        }, () -> {
            throw new IllegalArgumentException("Not Found: " + id);
        });

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

        PlayerData playerData = playerDataJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setHandElemental(handElemental);
        matchData.updatePlayer(playerData);

        playerDataJpaRepository.save(playerData);
        MatchData updMatchData = matchDataJPARepository.save(matchData);
        frdbRepository.setMatch(updMatchData.getId(), updMatchData);
         */
    }

    @Override
    public void upgradeElemental(UUID id, HAND_ELEMENTAL handElemental) {
        gameDataRepository.findPlayerById(id).ifPresentOrElse(playerData -> {
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

            gameDataRepository.savePlayer(playerData);
        }, () -> {
            throw new IllegalArgumentException("Not Found: " + id);
        });

        /*
        PlayerData playerData = playerDataJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + id));
        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
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

        playerDataJpaRepository.save(playerData);
        MatchData updMatchData = matchDataJPARepository.save(matchData);
        frdbRepository.setMatch(updMatchData.getId(), updMatchData);
         */
    }

    @Override
    public void receiveElementalAnimationEndAck(UUID playerId) {
        gameDataRepository.findPlayerById(playerId).ifPresentOrElse(playerData -> {
            if (playerData.getJoinedMatchId() != null) {
                gameDataRepository.findMatchById(playerData.getJoinedMatchId()).ifPresentOrElse(matchData -> {
                    if (matchData.getState() != MATCH_STATE.GAME_ELEMENTAL_RECEIVING) {
                        throw new IllegalStateException("Elemental receive animation ACK can be submitted only after turn result is calculated!");
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

            playerData.setAckState(ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END);
            gameDataRepository.savePlayer(playerData);
        }, () -> {
            throw new IllegalArgumentException("Player not Found: " + playerId);
        });

        /*
        PlayerData playerData = playerDataJpaRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));

        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        System.out.println(playerId + ": elemental receiving animation end-ack");

        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ELEMENTAL_RECEIVE_ANIMATION_END)) {
            System.out.println(matchData.getId() + ": round start!");
            // (라운드 리셋 + state 설정)
            matchService.initializeMatchRound(matchData);
        }

        playerDataJpaRepository.save(playerData);
        MatchData updMatchData = matchDataJPARepository.save(matchData);
        frdbRepository.setMatch(updMatchData.getId(), updMatchData);
        */
    }

}
