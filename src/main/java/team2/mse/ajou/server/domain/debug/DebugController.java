package team2.mse.ajou.server.domain.debug;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchRunnerService;

/**
 * DEBUG ONLY!!!
 * Debug database modification API endpoints.
 *
 * @author Ahn Yubin / 202021088
 */
@RestController
@RequestMapping("/debug")
public class DebugController {
    private final MatchRunnerService matchRunnerService;
    private final PlayerDataJpaRepository playerInfoRepository;
    private final MatchDataJpaRepository matchDataJpaRepository;

    public DebugController(
            MatchRunnerService matchRunnerService,
            PlayerDataJpaRepository playerInfoRepository,
            MatchDataJpaRepository matchDataJpaRepository
    ) {
        this.matchRunnerService = matchRunnerService;
        this.playerInfoRepository = playerInfoRepository;
        this.matchDataJpaRepository = matchDataJpaRepository;
    }

    /**
     * Try to delete all internal data and FRDB.
     */
    @DeleteMapping("/data")
    public void deleteData() {
        matchRunnerService.deleteAllMatches();
        matchDataJpaRepository.deleteAll();
        playerInfoRepository.deleteAll();
    }
}
