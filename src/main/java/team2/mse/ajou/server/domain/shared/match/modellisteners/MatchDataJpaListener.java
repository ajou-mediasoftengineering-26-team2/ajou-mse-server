package team2.mse.ajou.server.domain.shared.match.modellisteners;

import jakarta.persistence.PostUpdate;
import org.springframework.stereotype.Component;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.repository.GameObservablesRepository;

/**
 * 내부 DB에서 매치 Entity의 값이 바뀌면 (= DB상 값이 바뀌어 save까지 되는 시점) 호출되는 콜백을 처리합니다.
 * JPA의 리스너를 활용합니다.
 *
 * @author Ahn Yubin / 202021088
 */
@Component
public class MatchDataJpaListener {
    private final GameObservablesRepository eventsRepository;

    public MatchDataJpaListener(GameObservablesRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    @PostUpdate
    public void onMatchDataUpdate(MatchData matchData) {
        eventsRepository.sendMatchStateSwitch(matchData.getId(), matchData.getState());
    }
}
