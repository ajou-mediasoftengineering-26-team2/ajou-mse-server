package team2.mse.ajou.server.domain.debug;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchServiceLegacy;

/**
 * DEBUG ONLY!!!
 * Debug database modification API endpoints.
 *
 * @author Ahn Yubin / 202021088
 */
@RestController
@RequestMapping("/debug")
public class DebugController {
    private final MatchServiceLegacy matchService;
    private final PlayerDataJpaRepository playerInfoRepository;

    public DebugController(MatchServiceLegacy matchService, PlayerDataJpaRepository playerInfoRepository) {
        this.matchService = matchService;
        this.playerInfoRepository = playerInfoRepository;
    }

    /**
     * Try to delete all internal data and FRDB.
     */
    @DeleteMapping("/data")
    public void deleteData() {
        matchService.deleteAllMatch();
        playerInfoRepository.deleteAll();
    }
}
