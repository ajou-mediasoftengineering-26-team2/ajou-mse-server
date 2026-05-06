package team2.mse.ajou.server.domain.auth.controller;

import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.domain.auth.model.*;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.auth.service.AuthService;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * User login/authentication API endpoints.
 * Base URL: `<SERVER URL>/auth`
 *
 * @author Ahn Yubin / 202021088
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final PlayerDataRepository playerDataRepository;

    public AuthController(AuthService authService, PlayerDataRepository playerDataRepository) {
        this.authService = authService;
        this.playerDataRepository = playerDataRepository;
    }

    /**
     * Login with given username then joins (and creates if needed) a match.
     * Returns player and match (UU)ID.
     *
     * @param req Request body
     * @return Response body
     */
    @PostMapping("/player")
    public PostPlayerResponse postPlayer(
            @RequestBody PostPlayerRequest req
    ) {
        LoginAndJoinResult res = authService.loginAndJoin(req.playerName());

        return new PostPlayerResponse(
                res.playerId(),
                res.lobbyId()
        );
    }

    /**
     * Logs out player from given player UUID. Leaves ongoing match if the player is currently joining one.
     *
     * @param req Request body
     */
    @DeleteMapping("/player")
    public void deletePlayer(
            @RequestBody DeletePlayerRequest req
    ) {
        authService.logout(req.playerId());
    }

    /**
     * Checks whether given username is available.
     *
     * @param req Request body
     * @return Response body
     */
    @GetMapping("/player")
    public GetPlayerResponse getPlayer(
            @RequestBody GetPlayerRequest req
    ) {
        boolean res = authService.checkPlayerNameAvailable(req.username());
        return new GetPlayerResponse(res);
    }

    /**
     * Fetches list of all players.
     *
     * @return Response body
     */
    @GetMapping("/all-players")
    public GetAllPlayersResponse getAllPlayers() {
        List<PlayerData> playerData = playerDataRepository.findAll();
        return new GetAllPlayersResponse(playerData);
    }
}
