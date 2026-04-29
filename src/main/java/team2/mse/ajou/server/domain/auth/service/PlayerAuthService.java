package team2.mse.ajou.server.domain.auth.service;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.player.model.PlayerData;
import team2.mse.ajou.server.domain.shared.player.repository.PlayerDataRepository;

import java.util.UUID;

/**
 * 유저 로그인 관리 서비스.
 *
 * @author yubin
 */
@Service
public class PlayerAuthService {
    private final PlayerDataRepository playerDataRepository;

    public PlayerAuthService(PlayerDataRepository playerDataRepository) {
        this.playerDataRepository = playerDataRepository;
    }

    public boolean isUsernameAvailable(String username) {
        if (!isUsernameValid(username)) {
            return false;
        }
        return !playerDataRepository.existsByUsername(username);
    }

    public UUID login(String username) {
        if (!isUsernameAvailable(username)) {
            throw new IllegalArgumentException("사용 불가 닉네임.");
        }

        PlayerData playerData = new PlayerData();
        playerData.setUsername(username);

        PlayerData res = playerDataRepository.save(playerData);
        // System.out.println("SAVING PLAYERINFO FOR `%s`".formatted(res.getId()));

        return res.getId();
    }

    public void logout(UUID playerId) {
        playerDataRepository.deleteById(playerId);
        System.out.println("LOGOUT FOR `%s`".formatted(playerId));
    }

    public boolean isPlayerExists(UUID playerId) {
        return playerDataRepository.existsById(playerId);
    }

    private boolean isUsernameValid(@NonNull String username) {
        return !username.isEmpty();
    }
}
