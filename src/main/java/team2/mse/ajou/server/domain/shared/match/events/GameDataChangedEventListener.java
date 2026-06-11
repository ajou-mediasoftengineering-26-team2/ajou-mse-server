package team2.mse.ajou.server.domain.shared.match.events;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import team2.mse.ajou.server.domain.shared.match.repository.IGameDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.IGameObservablesRepository;

/**
 * Listener that handles `MatchDataChangedEvent` and `PlayerDataChangedEvent` sent from Spring Boot's `ApplicationEventPublisher`.
 * We do not directly send updates to `IGameDataRepository` in the JPA, and instead detour like this to make sure that the observers are running in the different thread than the one that has triggered the database changes.
 * Hopefully this make things run in parallel to make reactions to updates faster.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class GameDataChangedEventListener {
    private final IGameDataRepository gameDataRepository;
    private final IGameObservablesRepository gameObservablesRepository;

    public GameDataChangedEventListener(
            IGameDataRepository gameDataRepository,
            IGameObservablesRepository gameObservablesRepository
    ) {
        this.gameDataRepository = gameDataRepository;
        this.gameObservablesRepository = gameObservablesRepository;
    }

    /**
     * Handle `MatchDataChangedEvent` (MatchData DB update) for given ID
     */
    // Use `@TransactionalEventListener` to make sure that the event runs AFTER all DB operations. So the query would yield the actual changed data.
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

            // 플레이어 DB도 갱신
            // gameObservablesRepository.saveAll(matchData.getPlayers());
        });
    }

    /**
     * Handle `PlayerDataChangedEvent` (PlayerData DB update) for given ID
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePlayerDataChanged(PlayerDataChangedEvent event) {
        // System.out.println("handlePlayerDataChanged? -> " + event.playerId());
        // System.out.println("handlePlayerDataChanged!!!");
        gameDataRepository.findPlayerById(event.playerId()).ifPresent(playerData -> {
            // var copyPlayerData = new PlayerData(playerData);
            gameObservablesRepository.sendPlayerDataUpdate(playerData);
            //
            // UUID joinedMatchId = playerData.getJoinedMatchId();
            // ZonedDateTime updateTime = ZonedDateTime.now();
            //
            // // 매치 DB도 갱신
            // if (joinedMatchId != null) {
            //     gameDataRepository.findMatchById(joinedMatchId).ifPresent(matchData -> {
            //         System.out.println("sideeffect (player data), match state is " + matchData.getState() + " @ " + matchData.getLastUpdated() + " VS " + updateTime);
            //         if (updateTime.isAfter(matchData.getLastUpdated())) {
            //             System.out.println("\t(update!!)");
            //             matchData.updateLastUpdated();
            //         }
            //     });
            //     System.out.println("sideeffect end :)");
            // }
        });
    }
}
