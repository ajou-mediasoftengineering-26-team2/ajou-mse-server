package team2.mse.ajou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.subway.api.SubwayClass;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AjouMseServerApplication {

    private SubwayClass subwayClass;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        PlayerDataJpaRepository playerInfoRepository = ctx.getBean(PlayerDataJpaRepository.class);
        MatchDataJpaRepository lobbyInfoRepository = ctx.getBean(MatchDataJpaRepository.class);
        lobbyInfoRepository.deleteAll();
        playerInfoRepository.deleteAll();
    }

    @PostConstruct
    public void initTime() {
        // Set server timezone to KST.
        TimeZone.setDefault(TimeZone.getTimeZone("KST"));
    }
}
