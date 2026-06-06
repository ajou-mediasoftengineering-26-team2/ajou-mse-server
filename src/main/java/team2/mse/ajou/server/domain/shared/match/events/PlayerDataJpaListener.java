package team2.mse.ajou.server.domain.shared.match.events;

import jakarta.persistence.PostUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * 내부 DB에서 플레이어 Entity의 값이 바뀌면 (= DB상 값이 바뀌어 save까지 되는 시점) 호출되는 콜백을 처리합니다.
 * JPA의 리스너를 활용합니다.
 *
 * @author Ahn Yubin / 202021088
 */
@Component
public class PlayerDataJpaListener {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PlayerDataJpaListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostUpdate
    public void onMatchDataUpdate(PlayerData playerData) {
        applicationEventPublisher.publishEvent(new PlayerDataChangedEvent(playerData.getId()));
    }
}
