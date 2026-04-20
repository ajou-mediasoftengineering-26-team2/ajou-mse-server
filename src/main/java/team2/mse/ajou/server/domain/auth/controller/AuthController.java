package team2.mse.ajou.server.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.auth.model.*;
import team2.mse.ajou.server.domain.auth.repository.AuthRepository;
import team2.mse.ajou.server.domain.auth.repository.MatchmakingRepository;
import team2.mse.ajou.server.domain.auth.repository.PlayerInfoRepository;
import team2.mse.ajou.server.domain.auth.service.AuthService;
import team2.mse.ajou.server.domain.auth.service.LobbyService;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 로그인 / 인증 관련 API.
 * 베이스 URL: `<서버 주소>/auth`
 *
 * @author yubin
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final LobbyService lobbyService;

    private final AuthRepository authRepository;
    private final MatchmakingRepository matchmakingRepository;
    private final PlayerInfoRepository playerInfoRepository;

    public AuthController(AuthService authService, LobbyService lobbyService, AuthRepository authRepository, MatchmakingRepository matchmakingRepository, PlayerInfoRepository playerInfoRepository) {
        this.authService = authService;
        this.lobbyService = lobbyService;
        this.authRepository = authRepository;
        this.matchmakingRepository = matchmakingRepository;
        this.playerInfoRepository = playerInfoRepository;
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
        if (req.username().isBlank()) { // 이상한 입력값
            throw ApiError.INVALID_PARAMETER; // 자주 쓰이는 에러는 미리 정의된 상수 ApiError로 준비해봤습니다.
        } else if ("error".equalsIgnoreCase(req.username())) { // 그 외 에러
            throw new ApiError(67676767, "사용자가 에러를 원한대요 그래서 에러를 던져줬습니다. 67676767", HttpStatus.BAD_REQUEST);
        } else if ("error_unexpected".equalsIgnoreCase(req.username())) { // 예상치 못한 에러 (500)
            //throw new ArithmeticException();
            // 혹은
            int wow = 10 / 0; // ArithmeticException throw됨
        }

        String username = req.username();
        UUID playerId = null;

        try {
            playerId = authService.login(username);
        } catch (IllegalArgumentException e) {
            throw new ApiError(4000, "중복되는 닉네임입니다.");
        }

        LobbyInfo lobby = null;
        LobbyInfo previousLobby = lobbyService.getOpenLobby();

        // 참가 가능 로비가 없으니 새 로비 생성
        if (previousLobby == null) {
            lobby = lobbyService.createLobby();
        } else {
            lobby = previousLobby;
        }

        try {
            // createLobby() 도 실패하면 무슨 일이 생겨서 로비를 참가할수도 새로 생성할수도 없는 상황인 것... 이거는 버그일 가능성이 커요
            if (lobby == null) {
                throw new ApiError(5001, "로비 검색에 실패했습니다.");
            }

            // joinLobby()가 실패하는 것도 동일한 이치
            boolean result = lobbyService.joinLobby(playerId, lobby.getId());
            if (!result) {
                throw new ApiError(5002, "로비 참가에 실패했습니다.");
            }
        } catch (ApiError err) {
            // 뭐가되었든 로비 참가에 실패하면 자동으로 로그아웃 시켜줍시다
            authService.logout(playerId);
            throw err;
        }

        PostPlayerResponse res = new PostPlayerResponse(
                playerId,
                lobby.getId(),
                previousLobby == null
        );
        return res;
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
        UUID id = req.playerId();
        if (id == null) {
            throw ApiError.INVALID_PARAMETER;
        }

        LobbyInfo lobby = lobbyService.findPlayerLobby(id);

        if (lobby != null) {
            lobbyService.leaveLobby(id, lobby.getId());
        }

        if (!authService.isPlayerExists(id)) {
            throw new ApiError(4001, "로그인 되지 않은 플레이어입니다.");
        }
        authService.logout(id);

        System.out.printf("Player `%s` left the game!\n", id);
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
        String username = req.username();
        if (username == null) {
            throw ApiError.INVALID_PARAMETER;
        }

        boolean isUsernameAvailable = authService.isUsernameAvailable(username);
        GetPlayerResponse res = new GetPlayerResponse(isUsernameAvailable);
        return res;
    }

    /**
     * 모든 플레이어 목록을 가져옵니다.
     *
     * @return Response body
     */
    @GetMapping("/all-players")
    public GetAllPlayersResponse getAllPlayers() {
        List<PlayerInfo> playerInfos = playerInfoRepository.findAll();
        GetAllPlayersResponse res = new GetAllPlayersResponse(playerInfos);
        return res;
    }
}
