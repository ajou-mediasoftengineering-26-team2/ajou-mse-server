package team2.mse.ajou.server.domain.shared.match.events;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.GameObservablesRepository;

@Service
public class GameDataChangedEventListener {
    private final GameDataRepository gameDataRepository;
    private final GameObservablesRepository gameObservablesRepository;

    public GameDataChangedEventListener(
            GameDataRepository gameDataRepository,
            GameObservablesRepository gameObservablesRepository
    ) {
        this.gameDataRepository = gameDataRepository;
        this.gameObservablesRepository = gameObservablesRepository;
    }

    // @EventListener 대신 @TransactionalEventListener을 써서 DB에 결과가 실제로 적용된 이후에 실행되도록 해봅시다...
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchDataChanged(MatchDataChangedEvent event) {
        // System.out.println("handleMatchDataChanged? -> " + event.matchId());
        // System.out.println("handleMatchDataChanged!!!");
        gameDataRepository.findMatchById(event.matchId()).ifPresent(gameObservablesRepository::sendMatchDataUpdate);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePlayerDataChanged(PlayerDataChangedEvent event) {
        // System.out.println("handlePlayerDataChanged? -> " + event.playerId());
        // System.out.println("handlePlayerDataChanged!!!");
        gameDataRepository.findPlayerById(event.playerId()).ifPresent(gameObservablesRepository::sendPlayerDataUpdate);
    }
}
