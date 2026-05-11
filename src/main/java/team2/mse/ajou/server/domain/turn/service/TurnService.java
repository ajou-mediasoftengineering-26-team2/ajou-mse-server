package team2.mse.ajou.server.domain.turn.service;

import com.google.firebase.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
