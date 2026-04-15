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
            if (lobbyId == null) {
                throw new ApiError(5001, "로비 검색에 실패했습니다.");
            }
            if (!matchmakingRepository.joinLobby(playerToken, lobbyId)) {
                throw new ApiError(5002, "로비 참가에 실패했습니다.");
            }
        } catch (ApiError _) {
            authRepository.logout(playerToken);
        }

        PostPlayerResponse res = new PostPlayerResponse(
                playerToken,
                lobbyId,
                isLobbyNew
        );
        return res;
    }

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

    @GetMapping("/player")
    public GetPlayerResponse getPlayer(
            @RequestBody GetPlayerRequest req
    ) {
        boolean isUsernameAvailable = authRepository.isUsernameAvailable(req.username());
        GetPlayerResponse res = new GetPlayerResponse(isUsernameAvailable);
        return res;
    }
}
