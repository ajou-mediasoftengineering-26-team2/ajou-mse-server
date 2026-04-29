package team2.mse.ajou.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import team2.mse.ajou.server.domain.auth.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.player.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.firebase.service.FrdbMatchService;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class AjouMseServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        PlayerDataRepository playerInfoRepository = ctx.getBean(PlayerDataRepository.class);
        MatchDataRepository lobbyInfoRepository = ctx.getBean(MatchDataRepository.class);
        lobbyInfoRepository.deleteAll();
        playerInfoRepository.deleteAll();

        // FrdbMatchService frdbMatchService = ctx.getBean(FrdbMatchService.class);
        // frdbMatchService.clearAllMatch();
    }

    @PostConstruct
    public void initTime() {
        // 서버 시간대 한국(KST)으로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("KST"));
    }
}
