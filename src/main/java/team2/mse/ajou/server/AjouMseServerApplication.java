package team2.mse.ajou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import team2.mse.ajou.server.domain.auth.repository.LobbyInfoRepository;
import team2.mse.ajou.server.domain.auth.repository.PlayerInfoRepository;
import team2.mse.ajou.server.domain.auth.service.PlayerInfoService;

@SpringBootApplication
public class AjouMseServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        // PlayerInfoRepository playerInfoRepository = ctx.getBean(PlayerInfoRepository.class);
        // LobbyInfoRepository lobbyInfoRepository = ctx.getBean(LobbyInfoRepository.class);
        // lobbyInfoRepository.deleteAll();
        // playerInfoRepository.deleteAll();
    }

}
