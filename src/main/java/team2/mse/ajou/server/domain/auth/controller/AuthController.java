package team2.mse.ajou.server.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.auth.model.DeletePlayerRequest;
import team2.mse.ajou.server.domain.auth.model.PostPlayerRequest;
import team2.mse.ajou.server.domain.auth.model.PostPlayerResponse;

/**
 * 사용자 로그인 / 인증 관련 API.
 * 베이스 URL: `<서버 주소>/auth`
 *
 * @author yubin
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
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

        PostPlayerResponse res = new PostPlayerResponse(
                "TEST_TOKEN_" + req.username(),
                "TEST_ROOM_ID371232719"
        );
        return res;
    }

    @DeleteMapping("/player")
    public void deletePlayer(
            @RequestBody DeletePlayerRequest req
    ) {
        System.out.printf("Player `%s` left the game!\n", req.authToken());
    }
}
