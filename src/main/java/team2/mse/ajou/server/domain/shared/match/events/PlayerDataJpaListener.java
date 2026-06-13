package team2.mse.ajou.server.domain.shared.match.events;

import jakarta.persistence.PostUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;

/**
 * Listens to database changes for `PlayerData`, using JPA's listener support.
 * Usually happens when data is `save()`d.
 *
 * @author Ahn Yubin / 202021088
 */
@Component
public class PlayerDataJpaListener {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PlayerDataJpaListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Callback for `PlayerData` changes.
     *
     * @param playerData New `MatchData`.
     */
    @PostUpdate
    public void onPlayerDataUpdate(PlayerData playerData) {
        // Let the `GameDataChangedEventListener` handle it asynchronously!
        applicationEventPublisher.publishEvent(new PlayerDataChangedEvent(playerData.getId()));
    }
}
