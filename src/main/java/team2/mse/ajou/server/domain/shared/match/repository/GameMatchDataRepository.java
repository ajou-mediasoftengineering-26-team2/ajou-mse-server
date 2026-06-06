package team2.mse.ajou.server.domain.shared.match.repository;

import org.springframework.stereotype.Repository;
import team2.mse.ajou.server.domain.firebase.service.FrdbRepository;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.observer.DefaultObservable;
import team2.mse.ajou.server.domain.shared.observer.Observable;
import team2.mse.ajou.server.domain.shared.observer.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 게임 데이터 총괄하는 리포지토리.
 * 기존의 MatchData, PlayerData, FRDB를 따로 관리하니 생기는 이슈인 동시성 문제 등을 어느정도 완화시키기 위해 하나의 통일된 데이터 창구에서 관리하게 만들었습니다.
 * 내부적으로 (MySQL) DB에 접근하는 JPA 리포지토리, FRDB를 접근하는 리포지토리를 모두 사용합니다.
 *
 * @author Ahn Yubin / 202021088
 */
@Repository
public class GameMatchDataRepository implements GameDataRepository {
    private final Map<UUID, Observable<UUID>> matchPlayerJoinEventObservables;
    private final Map<UUID, Observable<UUID>> matchPlayerLeaveEventObservables;
    private final Map<UUID, Observable<ACK_TYPE>> playerAckEventObservables;

    // 리포지토리들
    private final MatchDataJpaRepository matchDataJPARepository;
    private final PlayerDataJpaRepository playerDataJpaRepository;
    private final FrdbRepository frdbRepository;

    public GameMatchDataRepository(
            MatchDataJpaRepository matchDataJPARepository,
            PlayerDataJpaRepository playerDataJpaRepository,
            FrdbRepository frdbRepository
    ) {
        this.matchPlayerJoinEventObservables = new HashMap<>();
        this.matchPlayerLeaveEventObservables = new HashMap<>();
        this.playerAckEventObservables = new HashMap<>();

        this.matchDataJPARepository = matchDataJPARepository;
        this.playerDataJpaRepository = playerDataJpaRepository;
        this.frdbRepository = frdbRepository;
    }

    @Override
    public Observable<UUID> getPlayerJoinEventsObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerJoinEventObservables, matchId, null, false, false);
    }

    @Override
    public Observable<UUID> getPlayerLeaveEventsObservable(UUID matchId) {
        return fetchOrCreateObservable(matchPlayerLeaveEventObservables, matchId, null, false, false);
    }

    @Override
    public Observable<ACK_TYPE> getPlayerAckEventsObservable(UUID playerId) {
        return fetchOrCreateObservable(playerAckEventObservables, playerId, ACK_TYPE.NO_ACK, true, true);
    }

    @Override
    public Optional<MatchData> findMatchById(UUID matchId) {
        return matchDataJPARepository.findById(matchId);
    }

    @Override
    public Optional<PlayerData> findPlayerById(UUID playerId) {
        return playerDataJpaRepository.findById(playerId);
    }

    @Override
    public MatchData saveMatchData(MatchData data) {
        return matchDataJPARepository.save(data);
    }

    @Override
    public PlayerData savePlayerData(PlayerData data) {
        return playerDataJpaRepository.save(data);
    }

    @Override
    public void updateFrdbMatchData(MatchData data) {
        frdbRepository.setMatch(data.getId(), data);
    }

    /**
     * Map에 주어진 ID값에 대응하는 Observable가 있으면 그것을 반환하고, 없으면 새로 생성해서 반환합니다.
     *
     * @param map
     * @param id
     * @param initialValue
     * @param isIgnoreDuplicateValue
     * @param isNotifyOnSubscribe
     * @param <T>
     * @return
     */
    private <T> Observable<T> fetchOrCreateObservable(
            Map<UUID, Observable<T>> map,
            UUID id,
            T initialValue,
            boolean isIgnoreDuplicateValue,
            boolean isNotifyOnSubscribe
    ) {
        map.putIfAbsent(id, new DefaultObservable<>(initialValue, isIgnoreDuplicateValue, isNotifyOnSubscribe));
        return map.get(id);
    }
}
