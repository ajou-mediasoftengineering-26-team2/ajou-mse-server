package team2.mse.ajou.server.domain.shared.match.events;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
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
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMatchDataChanged(MatchDataChangedEvent event) {
        // System.out.println("handleMatchDataChanged? -> " + event.matchId());
        // System.out.println("handleMatchDataChanged!!!");
        gameDataRepository.findMatchById(event.matchId()).ifPresent(matchData -> {
            // var copyMatchData = new MatchData(matchData);
            gameObservablesRepository.sendMatchDataUpdate(matchData);
        });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePlayerDataChanged(PlayerDataChangedEvent event) {
        // System.out.println("handlePlayerDataChanged? -> " + event.playerId());
        // System.out.println("handlePlayerDataChanged!!!");
        gameDataRepository.findPlayerById(event.playerId()).ifPresent(playerData -> {
            // var copyPlayerData = new PlayerData(playerData);
            gameObservablesRepository.sendPlayerDataUpdate(playerData);
        });
    }
}
