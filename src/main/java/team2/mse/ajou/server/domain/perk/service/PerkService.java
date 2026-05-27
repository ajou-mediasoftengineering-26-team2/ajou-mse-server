package team2.mse.ajou.server.domain.perk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

/**
 * Update the DB information about perk
 *
 *
 * @author Junseo Hwang 202322128
 */
@Service
@Deprecated(forRemoval = true)
public class PerkService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final MatchTurnCalcService matchTurnCalcService;
    private final MatchService matchService;
    private final FrdbService frdbService;

    @Autowired
    public PerkService(PlayerDataRepository playerDataRepository,
                       MatchDataRepository matchDataRepository,
                       MatchTurnCalcService matchTurnCalcService,
                       MatchService matchService,
                       FrdbService frdbService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.matchTurnCalcService = matchTurnCalcService;
        this.matchService = matchService;
        this.frdbService = frdbService;
    }


}
