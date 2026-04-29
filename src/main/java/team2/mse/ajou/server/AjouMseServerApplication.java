package team2.mse.ajou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import team2.mse.ajou.server.domain.subway.api.SubwayClass;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableScheduling
public class AjouMseServerApplication {

    private SubwayClass subwayClass;
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        // PlayerInfoRepository playerInfoRepository = ctx.getBean(PlayerInfoRepository.class);
        // LobbyInfoRepository lobbyInfoRepository = ctx.getBean(LobbyInfoRepository.class);
        // lobbyInfoRepository.deleteAll();
        // playerInfoRepository.deleteAll();
    }

}
