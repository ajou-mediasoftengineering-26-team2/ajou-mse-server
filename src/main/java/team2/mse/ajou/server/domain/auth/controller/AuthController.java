package team2.mse.ajou.server.domain.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.auth.model.*;
import team2.mse.ajou.server.domain.auth.repository.AuthRepository;
import team2.mse.ajou.server.domain.auth.repository.MatchmakingRepository;

/**
 * 사용자 로그인 / 인증 관련 API.
 * 베이스 URL: `<서버 주소>/auth`
 *
 * @author yubin
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private MatchmakingRepository matchmakingRepository;

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

        String playerToken = authRepository.login(req.username());

        if (playerToken == null) {
            throw new ApiError(4000, "중복되는 닉네임입니다.");
        }

        String lobbyId = matchmakingRepository.getOpenLobby();
        boolean isLobbyNew = false;

        // 참가 가능 로비가 없으니 새 로비 생성
        if (lobbyId == null) {
            lobbyId = matchmakingRepository.createLobby();
            isLobbyNew = true;
        }

        try {
            // createLobby() 도 실패하면 무슨 일이 생겨서 로비를 참가할수도 새로 생성할수도 없는 상황인 것... 이거는 버그일 가능성이 커요
            if (lobbyId == null) {
                throw new ApiError(5001, "로비 검색에 실패했습니다.");
            }

            // joinLobby()가 실패하는 것도 동일한 이치
            boolean result = matchmakingRepository.joinLobby(playerToken, lobbyId);
            if (!result) {
                throw new ApiError(5002, "로비 참가에 실패했습니다.");
            }
        } catch (ApiError _) {
            // 뭐가되었든 로비 참가에 실패하면 자동으로 로그아웃 시켜줍시다
            authRepository.logout(playerToken);
        }

        PostPlayerResponse res = new PostPlayerResponse(
                playerToken,
                lobbyId,
                isLobbyNew
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
        String playerToken = req.playerToken();
        String username = authRepository.getPlayerData(playerToken);
        String lobbyId = matchmakingRepository.findPlayerLobby(playerToken);

        if (lobbyId != null) {
            matchmakingRepository.leaveLobby(playerToken, lobbyId);
        }

        if (!authRepository.logout(playerToken)) {
            throw new ApiError(4001, "로그인 되지 않은 플레이어입니다.");
        }

        System.out.printf("Player `%s` left the game!\n", username);
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
        boolean isUsernameAvailable = authRepository.isUsernameAvailable(req.username());
        GetPlayerResponse res = new GetPlayerResponse(isUsernameAvailable);
        return res;
    }
}
