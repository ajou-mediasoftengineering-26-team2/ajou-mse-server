package team2.mse.ajou.server.domain.auth.controller;

import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.domain.auth.model.*;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.auth.service.AuthService;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

import java.util.List;

/**
 * 사용자 로그인 / 인증 관련 API.
 * 베이스 URL: `<서버 주소>/auth`
 *
 * @author Ahn yubin / 202021088
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
     * 주어진 닉네임으로 로그인하고, 로비에 입장하거나 새로운 로비를 생성합니다.
     * 플레이어 고유식별자인 플레이어 토큰과, 로비 고유식별자인 로비 ID, 그리고 로비가 새로 생성되어 다른 플레이어 대기가 필요한지를 응답으로 내려줍니다.
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
     * 로그인 된 플레이어 토큰으로 로그아웃 합니다. 참가중인 로비가 있으면 나갑니다.
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
     * 주어진 닉네임이 이미 사용 중인지 확인합니다.
     * 사용 가능한지 여부를 응답으로 내려줍니다.
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
     * 모든 플레이어 목록을 가져옵니다.
     *
     * @return Response body
     */
    @GetMapping("/all-players")
    public GetAllPlayersResponse getAllPlayers() {
        List<PlayerData> playerData = playerDataRepository.findAll();
        return new GetAllPlayersResponse(playerData);
    }
}
