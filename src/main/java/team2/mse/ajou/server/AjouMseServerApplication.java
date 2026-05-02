package team2.mse.ajou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import team2.mse.ajou.server.domain.subway.api.SubwayClass;
import org.springframework.context.ConfigurableApplicationContext;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class AjouMseServerApplication {

    private SubwayClass subwayClass;
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        MatchService matchService = ctx.getBean(MatchService.class);
        PlayerDataRepository playerInfoRepository = ctx.getBean(PlayerDataRepository.class);
        MatchDataRepository lobbyInfoRepository = ctx.getBean(MatchDataRepository.class);

        List<MatchData> allMatches = lobbyInfoRepository.findAll();
        List<PlayerData> allPlayers = playerInfoRepository.findAll();

        System.out.println("player: " + allPlayers.size());
        for (PlayerData player: allPlayers) {
            System.out.println("\tdisconnect " + player.getUsername() + " / " + matchService.leaveMatch(player.getId(), player.getJoinedMatchId()));
        }

        System.out.println("lobbies: " + allMatches.size());
        for (MatchData match: allMatches) {
            System.out.println("\tlobby: " + match);
        }

        lobbyInfoRepository.deleteAll();
        playerInfoRepository.deleteAll();

        FrdbService frdbService = ctx.getBean(FrdbService.class);
        frdbService.clearAllMatch();
    }

    @PostConstruct
    public void initTime() {
        // 서버 시간대 한국(KST)으로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("KST"));
    }
}
