package team2.mse.ajou.server.domain.auth.service;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.auth.model.LoginAndJoinResult;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchServiceLegacy;

import java.util.UUID;

/**
 * Player login & matchmaking related service.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class AuthService {
    private final PlayerDataJpaRepository playerDataJpaRepository;
    private final MatchServiceLegacy matchService;

    public AuthService(PlayerDataJpaRepository playerDataJpaRepository, MatchServiceLegacy matchService) {
        this.playerDataJpaRepository = playerDataJpaRepository;
        this.matchService = matchService;
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

        MatchData lobby = null;
        UUID playerId = null;

        try {
            try {
                playerId = forceLogin(playerName);
            } catch (IllegalArgumentException e) {
                throw new ApiError(4000, "Username unavailable.");
            }

            MatchData previousLobby = matchService.getOpenMatch();

            if (previousLobby != null) {
                lobby = previousLobby;
            } else {
                // Create a new lobby/match if there's no lobby to join
                lobby = matchService.createMatch();
            }

            // If `createMatch()` fails, then we have problem finding lobbies to join. Mostly a bug.
            if (lobby == null) {
                throw new ApiError(5001, "Failed to search for lobby.");
            }

            // Same thing goes for `joinMatch()` failing.
            boolean result = matchService.joinMatch(playerId, lobby.getId());
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

        if (lobby == null) { // This is theoretically un-reachable condition. But just in case.
            throw new ApiError(5000, "Lobby error. (FATAL ERROR!! CALL YUBIN)");
        }

        return new LoginAndJoinResult(
                playerId,
                lobby.getId()
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

        // 1] Log out player if they are currently in match first.
        MatchData lobby = matchService.findMatchByPlayerId(playerId);
        if (lobby != null) {
            matchService.leaveMatch(playerId, lobby.getId());
        }

        // 2] Log out player if they are currently are.
        if (!isPlayerLoggedIn(playerId)) {
            throw new ApiError(4001, "User not logged in.");
        }
        forceLogout(playerId);

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
        return !playerDataJpaRepository.existsByUsername(playerName);
    }

    private void forceLogout(UUID playerId) {
        playerDataJpaRepository.deleteById(playerId);
        System.out.println("LOGOUT FOR `%s`".formatted(playerId));
    }

    private UUID forceLogin(String username) {
        if (!checkPlayerNameAvailable(username)) {
            throw new IllegalArgumentException("Username unavailable.");
        }

        PlayerData playerData = new PlayerData();
        // FIXME: Add player ready button in the lobby / waiting screen
        playerData.setReady(true);

        playerData.setUsername(username);

        PlayerData res = playerDataJpaRepository.save(playerData);
        // System.out.println("SAVING PLAYERINFO FOR `%s`".formatted(res.getId()));

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

    /**
     * Checks whether player with given ID is currently logged in.
     *
     * @param playerId Player UUID.
     * @return Whether given player is logged in.
     */
    private boolean isPlayerLoggedIn(UUID playerId) {
        return playerDataJpaRepository.existsById(playerId);
    }
}
