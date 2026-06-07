package team2.mse.ajou.server.domain.ack.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;

import java.util.UUID;

/**
 * ack 테스트
 */
@Service
public class AckTestService {
    private final GameDataRepository gameDataRepository;

    public AckTestService(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Transactional
    public void putAck(UUID playerId, ACK_TYPE type) {
        gameDataRepository.findPlayerById(playerId).ifPresent(playerData -> {
            playerData.setAckState(type);
            gameDataRepository.savePlayer(playerData);
        });
    }
}
