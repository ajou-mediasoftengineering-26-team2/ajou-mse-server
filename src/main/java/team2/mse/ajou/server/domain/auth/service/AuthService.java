package team2.mse.ajou.server.domain.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.auth.model.LoginAndJoinResult;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;

import java.util.UUID;

/**
 * 플레이어 로그인 & 로비 접속 인터랙션 관련 기능들 담당 서비스.
 *
 * @author Ahn yubin / 202021088
 */
@Service
public class AuthService {
    private final PlayerAuthService playerAuthService;
    private final MatchService matchService;

    public AuthService(PlayerAuthService playerAuthService, MatchService matchService) {
        this.playerAuthService = playerAuthService;
        this.matchService = matchService;
    }

    public LoginAndJoinResult loginAndJoin(String playerName) {
        if (playerName == null || playerName.isBlank()) { // 이상한 입력값
            throw ApiError.INVALID_PARAMETER; // 자주 쓰이는 에러는 미리 정의된 상수 ApiError로 준비해봤습니다.
        } else if ("error".equalsIgnoreCase(playerName)) { // 그 외 에러
            throw new ApiError(67676767, "사용자가 에러를 원한대요 그래서 에러를 던져줬습니다. 67676767", HttpStatus.BAD_REQUEST);
        } else if ("error_unexpected".equalsIgnoreCase(playerName)) { // 예상치 못한 에러 (500)
            // throw new ArithmeticException();
            // 혹은
            int wow = 10 / 0; // ArithmeticException throw됨
        }

        MatchData lobby = null;
        UUID playerId = null;

        try {
            try {
                playerId = playerAuthService.login(playerName);
            } catch (IllegalArgumentException e) {
                throw new ApiError(4000, "중복되는 닉네임입니다.");
            }

            MatchData previousLobby = matchService.getOpenMatch();

            if (previousLobby != null) {
                lobby = previousLobby;
            } else {
                // 참가 가능 로비가 없으니 새 로비 생성
                lobby = matchService.createMatch();
            }

            // createLobby() 도 실패하면 무슨 일이 생겨서 로비를 참가할수도 새로 생성할수도 없는 상황인 것... 이거는 버그일 가능성이 커요
            if (lobby == null) {
                throw new ApiError(5001, "로비 검색에 실패했습니다.");
            }

            // joinLobby()가 실패하는 것도 동일한 이치
            boolean result = matchService.joinMatch(playerId, lobby.getId());
            if (!result) {
                throw new ApiError(5002, "로비 참가에 실패했습니다.");
            }
        } catch (ApiError err) {
            // 뭐가되었든 로비 참가에 실패하면 자동으로 로그아웃 시켜줍시다
            if (playerId != null) {
                playerAuthService.logout(playerId);
            }
            throw err;
        }

        if (lobby == null) { // 이미 위에서 throw로 가드를 해줘서 사실상 진입 불가능합니다. 그래도 혹시나..
            throw new ApiError(5000, "로비 에러. (실제로는 불가능한 에러입니다 만약 이게 내려오면 알려주세요!!)");
        }

        return new LoginAndJoinResult(
                playerId,
                lobby.getId()
        );
    }

    public void logout(UUID playerId) {
        if (playerId == null) {
            throw ApiError.INVALID_PARAMETER;
        }

        // 1] 플레이어가 로비에 입장해있는 경우 퇴장
        MatchData lobby = matchService.findMatchByPlayerId(playerId);
        if (lobby != null) {
            matchService.leaveMatch(playerId, lobby.getId());
        }

        // 2] 그 뒤에서야 플레이어 로그인 여부 판단 & 로그아웃 진행
        if (!playerAuthService.isPlayerExists(playerId)) {
            throw new ApiError(4001, "로그인 되지 않은 플레이어입니다.");
        }
        playerAuthService.logout(playerId);

        System.out.printf("Player `%s` left the game!\n", playerId);
    }

    public boolean checkPlayerNameAvailable(String playerName) {
        if (playerName == null) {
            throw ApiError.INVALID_PARAMETER;
        }

        return playerAuthService.isUsernameAvailable(playerName);
    }
}
