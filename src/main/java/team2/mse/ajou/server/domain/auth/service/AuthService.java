package team2.mse.ajou.server.domain.auth.service;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.auth.model.LoginAndJoinResult;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchRunnerService;

import java.util.UUID;

/**
 * Player login & matchmaking related service.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class AuthService {
    private final GameDataRepository gameDataRepository;
    private final MatchRunnerService matchRunnerService;

    public AuthService(
            GameDataRepository gameDataRepository,
            MatchRunnerService matchRunnerService
    ) {
        this.gameDataRepository = gameDataRepository;
        this.matchRunnerService = matchRunnerService;
    }

    /**
     * Login with given username then joins (and creates if needed) a match.
     * Returns player and match (UU)ID.
     *
     * @param playerName Player username.
     * @return Result data.
     */
    @Transactional
    public LoginAndJoinResult loginAndJoin(String playerName) {
        if (playerName == null || playerName.isBlank()) { // Invalid parameter
            throw ApiError.INVALID_PARAMETER; // Use constant/pre-made ApiError for common errors
        } else if ("error".equalsIgnoreCase(playerName)) { // Misc. error scenario
            throw new ApiError(67676767, "ERROR TEST", HttpStatus.BAD_REQUEST);
        } else if ("error_unexpected".equalsIgnoreCase(playerName)) { // Artificial (?) unexpected error (500) test scenario
            // throw new ArithmeticException();
            // OR
            int wow = 10 / 0; // ArithmeticException is thrown here
        }

        UUID matchId = null;
        UUID playerId = null;

        try {
            try {
                playerId = forceLogin(playerName);
            } catch (IllegalArgumentException e) {
                throw new ApiError(4000, "Username unavailable.");
            }

            UUID previousLobby = matchRunnerService.findOpenMatch();

            if (previousLobby != null) {
                matchId = previousLobby;
            } else {
                // Create a new lobby/match if there's no lobby to join
                matchId = matchRunnerService.createNewMatch();
            }

            // If `createMatch()` fails, then we have problem finding lobbies to join. Mostly a bug.
            if (matchId == null) {
                throw new ApiError(5001, "Failed to search for lobby.");
            }

            // Same thing goes for `joinMatch()` failing.
            boolean result = matchRunnerService.joinPlayerToMatch(playerId, matchId);
            if (!result) {
                throw new ApiError(5002, "Failed to enter lobby.");
            }
        } catch (ApiError err) {
            // If (creating &) joining lobbies have failed on both end, log the player out to make it available to be used so players may try again.
            if (playerId != null) {
                forceLogout(playerId);
            }
            throw err;
        }

        if (matchId == null) { // This is theoretically un-reachable condition. But just in case.
            throw new ApiError(5000, "Lobby error. (FATAL ERROR!! CALL YUBIN)");
        }

        return new LoginAndJoinResult(
                playerId,
                matchId
        );
    }

    /**
     * Logs out player from given player UUID. Leaves ongoing match if the player is currently joining one.
     *
     * @param playerId Player UUID.
     */
    @Transactional
    public void logout(UUID playerId) {
        if (playerId == null) {
            throw ApiError.INVALID_PARAMETER;
        }

        // 1] 이미 플레이어가 매치에 입장해있는 상태면 내보내기.
        // 1] Log out player if they are currently in match first.
        var joinedMatch = gameDataRepository.findMatchByJoinedPlayerId(playerId);
        joinedMatch.ifPresent(matchData -> matchRunnerService.leavePlayerFromMatch(playerId, matchData.getId()));

        // 2] 플레이어 로그인시에만 로그아웃.
        // 2] Log out player if they are currently are.
        if (gameDataRepository.isPlayerExistsById(playerId)) {
            forceLogout(playerId);
            // throw new ApiError(4001, "User not logged in.");
        }

        System.out.printf("Player `%s` left the game!\n", playerId);
    }

    /**
     * Checks whether given username is available.
     *
     * @param playerName Username.
     * @return Whether given username is available.
     */
    public boolean checkPlayerNameAvailable(String playerName) {
        if (playerName == null) {
            throw ApiError.INVALID_PARAMETER;
        }

        if (!isUsernameValid(playerName)) {
            return false;
        }
        return !gameDataRepository.isPlayerExistsByUsername(playerName);
    }

    /**
     * (내부용) 강제 로그아웃
     *
     * @param playerId
     */
    private void forceLogout(UUID playerId) {
        gameDataRepository.deletePlayerById(playerId);
        System.out.printf("[PLR: %s] PLAYER LOGOUT~~~~!!\n", playerId);
    }

    /**
     * (내부용) 닉네임 체크 & 강제 로그인
     *
     * @param username
     * @return
     */
    private UUID forceLogin(String username) {
        if (!checkPlayerNameAvailable(username)) {
            throw new IllegalArgumentException("Username unavailable.");
        }

        PlayerData playerData = new PlayerData();
        // TODO: Add player ready button in the lobby / waiting screen
        playerData.setReady(true);
        playerData.setUsername(username);

        PlayerData res = gameDataRepository.savePlayer(playerData);
        System.out.printf("[PLR: %s] PLAYER LOGIN~~~~!!\n", res.getId());

        return res.getId();
    }

    /**
     * Checks whether given username is in valid format.
     *
     * @param username Username.
     * @return Whether given username is valid.
     */
    private boolean isUsernameValid(@NonNull String username) {
        return !username.isEmpty();
    }
}
