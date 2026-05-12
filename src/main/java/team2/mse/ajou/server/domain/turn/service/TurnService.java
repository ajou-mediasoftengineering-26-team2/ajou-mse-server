package team2.mse.ajou.server.domain.turn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;

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

    @Autowired
    public TurnService(PlayerDataRepository playerDataRepository) {
        this.playerDataRepository = playerDataRepository;
    }

    /**
     * 플레이어의 선택을 playerDatabase에 저장
     *
     * @param id player's uuid
     * @param choice hand action which player choose
     * @throws Exception
     *
     */
    public void putPlayerInput(String id, String choice) throws Exception {
        UUID uuid = UUID.fromString(id);
        PlayerData playerData = playerDataRepository.findById(uuid)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));

        // When a request is received while it is not the player’s turn.
        if (!playerData.isSelecting()) {
            throw new IllegalStateException("Player is not selecting!");
        }

        HAND_CHOICE handChoice = HAND_CHOICE.valueOf(choice);
        playerData.setChoice(handChoice);

        playerDataRepository.save(playerData);
    }


    public void receiveTurnAnimationEndAck(String id) throws Exception {
        UUID uuid = UUID.fromString(id);
        PlayerData playerData = playerDataRepository.findById(uuid)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+id));

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        playerData.setAckState(ACK_TYPE.TURN_ANIMATION_END);

        // TODO: if all players send ack, start next turn(start 5 sec timer)

        playerDataRepository.save(playerData);
    }
}
