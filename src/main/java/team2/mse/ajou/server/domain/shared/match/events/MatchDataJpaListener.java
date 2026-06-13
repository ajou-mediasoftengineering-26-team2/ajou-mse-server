package team2.mse.ajou.server.domain.shared.match.events;

import jakarta.persistence.PostUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;

/**
 * Listens to database changes for `MatchData`, using JPA's listener support.
 * Usually happens when data is `save()`d.
 *
 * @author Ahn Yubin / 202021088
 */
@Component
public class MatchDataJpaListener {
    private final ApplicationEventPublisher applicationEventPublisher;

    public MatchDataJpaListener(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Callback for `MatchData` changes.
     *
     * @param matchData New `MatchData`.
     */
    @PostUpdate
    public void onMatchDataUpdate(MatchData matchData) {
        // Let the `GameDataChangedEventListener` handle it asynchronously!
        applicationEventPublisher.publishEvent(new MatchDataChangedEvent(matchData.getId()));
    }
}
