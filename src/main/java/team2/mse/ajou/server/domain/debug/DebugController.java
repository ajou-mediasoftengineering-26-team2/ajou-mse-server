package team2.mse.ajou.server.domain.debug;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;

/**
 * 디버그용!!!
 * 디버그용 데이터 조종 컨트롤러
 *
 * @author Ahn Yubin / 202021088
 */
@RestController
@RequestMapping("/debug")
public class DebugController {
    private final MatchService matchService;
    private final PlayerDataRepository playerInfoRepository;

    public DebugController(MatchService matchService, PlayerDataRepository playerInfoRepository) {
        this.matchService = matchService;
        this.playerInfoRepository = playerInfoRepository;
    }

    /**
     * 서버 DB의 모든 데이터를 지우고 리셋시킵니다. 아마도
     */
    @DeleteMapping("/data")
    public void deleteData() {
        matchService.deleteAllMatch();
        playerInfoRepository.deleteAll();
    }
}
