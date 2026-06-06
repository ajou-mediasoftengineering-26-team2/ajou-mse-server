package team2.mse.ajou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchServiceLegacy;
import team2.mse.ajou.server.domain.subway.api.SubwayClass;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AjouMseServerApplication {

    private SubwayClass subwayClass;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        MatchServiceLegacy matchService = ctx.getBean(MatchServiceLegacy.class);
        PlayerDataJpaRepository playerInfoRepository = ctx.getBean(PlayerDataJpaRepository.class);
        MatchDataJpaRepository lobbyInfoRepository = ctx.getBean(MatchDataJpaRepository.class);

        List<MatchData> allMatches = lobbyInfoRepository.findAll();
        List<PlayerData> allPlayers = playerInfoRepository.findAll();

        System.out.println("player: " + allPlayers.size());
        for (PlayerData player : allPlayers) {
            System.out.println("\tdisconnect " + player.getUsername() + " / " + matchService.leaveMatch(player.getId(), player.getJoinedMatchId()));
        }

        System.out.println("lobbies: " + allMatches.size());
        for (MatchData match : allMatches) {
            System.out.println("\tlobby: " + match);
        }

        lobbyInfoRepository.deleteAll();
        playerInfoRepository.deleteAll();
        /*
        FrdbService frdbService = ctx.getBean(FrdbService.class);
        frdbService.clearAllMatch();
        */
    }

    @PostConstruct
    public void initTime() {
        // Set server timezone to KST.
        TimeZone.setDefault(TimeZone.getTimeZone("KST"));
    }
}
