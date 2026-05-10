package team2.mse.ajou.server.domain.auth.service;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.auth.model.PlayerInfo;
import team2.mse.ajou.server.domain.auth.repository.PlayerInfoRepository;

import java.util.UUID;

/**
 * 유저 로그인 관리 서비스.
 *
 * @author yubin
 */
@Service
public class PlayerInfoService {
    private final PlayerInfoRepository playerInfoRepository;

    public PlayerInfoService(PlayerInfoRepository playerInfoRepository) {
        this.playerInfoRepository = playerInfoRepository;
    }

    public boolean isUsernameAvailable(String username) {
        if (!isUsernameValid(username)) {
            return false;
        }
        return !playerInfoRepository.existsByUsername(username);
    }

    public UUID login(String username) {
        if (!isUsernameAvailable(username)) {
            throw new IllegalArgumentException("사용 불가 닉네임.");
        }

        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setUsername(username);

        PlayerInfo res = playerInfoRepository.save(playerInfo);
        // System.out.println("SAVING PLAYERINFO FOR `%s`".formatted(res.getId()));

        return res.getId();
    }

    public void logout(UUID playerId) {
        playerInfoRepository.deleteById(playerId);
        System.out.println("LOGOUT FOR `%s`".formatted(playerId));
    }

    public boolean isPlayerExists(UUID playerId) {
        return playerInfoRepository.existsById(playerId);
    }

    private boolean isUsernameValid(@NonNull String username) {
        return !username.isEmpty();
    }
}
