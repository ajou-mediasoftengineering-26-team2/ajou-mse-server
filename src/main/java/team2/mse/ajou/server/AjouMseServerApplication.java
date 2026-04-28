package team2.mse.ajou.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import team2.mse.ajou.server.domain.auth.repository.LobbyDataRepository;
import team2.mse.ajou.server.domain.auth.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.firebase.lobby.FrdbLobbyService;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class AjouMseServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(AjouMseServerApplication.class, args);

        // TEST
        PlayerDataRepository playerInfoRepository = ctx.getBean(PlayerDataRepository.class);
        LobbyDataRepository lobbyInfoRepository = ctx.getBean(LobbyDataRepository.class);
        lobbyInfoRepository.deleteAll();
        playerInfoRepository.deleteAll();

        FrdbLobbyService frdbLobbyService = ctx.getBean(FrdbLobbyService.class);
        frdbLobbyService.clearAllLobby();
    }

    @PostConstruct
    public void initTime() {
        // 서버 시간대 한국(KST)으로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("KST"));
    }
}
