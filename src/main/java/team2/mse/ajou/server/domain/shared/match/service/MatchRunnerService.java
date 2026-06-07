package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.domain.firebase.FrdbConstants;
import team2.mse.ajou.server.domain.item.service.ItemService;
import team2.mse.ajou.server.domain.perk.service.PerkService;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.GameObservablesRepository;
import team2.mse.ajou.server.domain.subway.repository.StationRepository;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static team2.mse.ajou.server.domain.firebase.FrdbConstants.TIME_ZONE_ID;

/**
 * 매치를 관리하며, 매치에서 사용하는 Observer 연결, 타이머 관리 등 로직을 실행하는 서비스.
 * Service for running logics for match.
 *
 * @author Ahn Yubin / 202021088
 */
@Service
public class MatchRunnerService {
    // 매치 로직 (데이터 리셋, 턴 계산 등) Delegate
    MatchTurnCalcService matchTurnCalcService;
    ItemService itemService;

    // 현재 관리중인 (i.e. 옵저버가 돌아가는) 매치들
    Map<UUID, RunningMatch> allRunningMatches;

    // 리포지토리들
    private final GameObservablesRepository gameEventsRepository;
    private final GameDataRepository gameDataRepository;
    private final StationRepository stationRepository;

    // 매치별 타이머 실행용 TaskScheduler
    private final TaskScheduler scheduler;

    // 뮤텍스
    private final ReentrantLock mutex;

    public MatchRunnerService(
            MatchTurnCalcService matchTurnCalcService,
            ItemService itemService,
            GameObservablesRepository gameEventsRepository,
            GameDataRepository gameDataRepository,
            StationRepository stationRepository
    ) {
        this.matchTurnCalcService = matchTurnCalcService;
        this.itemService = itemService;

        this.gameEventsRepository = gameEventsRepository;
        this.gameDataRepository = gameDataRepository;
        this.stationRepository = stationRepository;

        this.allRunningMatches = new HashMap<>();
        this.mutex = new ReentrantLock();
        this.scheduler = new SimpleAsyncTaskScheduler();
    }

    @Transactional
    public UUID createNewMatch() {
        // DB에 저장
        var matchData = new MatchData();

        String station = stationRepository.getStation();
        matchData.setStation(station);

        var newMatchData = gameDataRepository.saveMatch(matchData);
        var newMatchId = newMatchData.getId();

        System.out.printf("[MATCH] MatchRunnerService::createNewMatch | NEW MATCH ID = %s\n", newMatchId);
        setupRunningMatch(newMatchId);

        return newMatchId;
    }

    @Transactional
    public void deleteMatch(UUID matchId) {
        System.out.printf("[MATCH] MatchRunnerService::deleteMatch | TRY DELETING MATCH! (%s)\n", matchId);

        freeRunningMatch(matchId);
        gameDataRepository.deleteMatchById(matchId);
    }

    @Transactional
    public void deleteAllMatches() {
        for (var matchId : allRunningMatches.keySet()) {
            deleteMatch(matchId);
        }

        allRunningMatches.clear();
    }

    @Transactional
    public UUID findOpenMatch() {
        return gameDataRepository.findAllMatches()
                .stream()
                .filter(match -> match.getPlayers().size() < 2 && match.getState() == MATCH_STATE.LOBBY_WAITING)
                .map(MatchData::getId)
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public boolean joinPlayerToMatch(UUID playerId, UUID matchId) {
        var matchData = gameDataRepository.findMatchById(matchId).orElse(null);
        var playerData = gameDataRepository.findPlayerById(playerId).orElse(null);
        var data = allRunningMatches.getOrDefault(matchId, null);

        if (matchData == null) {
            System.out.printf("[MATCH] MatchRunnerService::joinPlayerToMatch | MATCH NOT FOUND IN DB! (%s)\n", matchId);
            return false;
        }
        if (playerData == null) {
            System.out.printf("[MATCH] MatchRunnerService::joinPlayerToMatch | PLAYER NOT FOUND IN DB! (%s)\n", playerId);
            return false;
        }
        if (data == null) {
            System.out.printf("[MATCH] MatchRunnerService::joinPlayerToMatch | MATCH NOT FOUND IN allRunningMatches! (%s)\n", matchId);
            return false;
        }

        // If game has already started, abort.
        if (matchData.getState().isIngame()) {
            return false;
        }

        // 플레이어: 현재 매치 ID 갱신
        // Add player to the list of joined player for match, and set joined match for player.
        playerData.setJoinedMatchId(matchId);
        matchData.updatePlayer(playerData);
        // (뭔가 바뀌는 값이 있어야 콜백이 도므로 나중에 리셋해줄 attackingPlayer 인덱스값을 임의로 설정합니다. 이거 지우면 큰일나요!!)
        matchData.setAttackerPlayerIdx(matchData.getPlayers().size());

        System.out.printf("[MATCH] MatchRunnerService::joinPlayerToMatch | PLAYER (%s) JOINED! (%s) -> NEW PLAYERS = [%s]\n",
                playerId,
                matchId,
                matchData.getPlayers().stream()
                        .map(player -> "%s <%s>".formatted(player.getUsername(), player.getId()))
                        .collect(Collectors.joining(", "))
        );

        // 플레이어 입장 이벤트 발행 & ACK 등 플레이어 단위 옵저버 추가 연결
        // Send match join event & Connect observers.
        gameEventsRepository.sendMatchPlayerJoinEvent(matchId, playerId);
        data.subscribeToPlayerAckEvents(playerId, gameEventsRepository.getPlayerAckEventsObservable(playerId));
        data.subscribeToPlayerDataUpdates(playerId, gameEventsRepository.getPlayerDataObservable(playerId));

        // 내부 DB 갱신
        // Update internal DB & Firebase RDB to reflect this change.
        var newMatchData = gameDataRepository.saveMatch(matchData);
        gameDataRepository.updateFrdbMatchData(newMatchData);
        return true;
    }

    @Transactional
    public boolean leavePlayerFromMatch(UUID playerId, UUID matchId) {
        if (playerId == null || matchId == null) {
            System.out.printf("[MATCH] MatchRunnerService::leavePlayerFromMatch | NULL PARAMETER\n");
            return false;
        }

        var matchData = gameDataRepository.findMatchById(matchId).orElse(null);
        var data = allRunningMatches.getOrDefault(matchId, null);

        if (matchData == null) {
            System.out.printf("[MATCH] MatchRunnerService::leavePlayerFromMatch | MATCH NOT FOUND IN DB! (%s)\n", matchId);
            return false;
        }
        if (data == null) {
            System.out.printf("[MATCH] MatchRunnerService::leavePlayerFromMatch | MATCH NOT FOUND IN allRunningMatches! (%s)\n", matchId);
            return false;
        }

        var playerData = matchData.findPlayerById(playerId);

        // 플레이어: 현재 매치 ID 갱신
        // Remove player from match.
        playerData.setJoinedMatchId(null);
        playerData.setReady(false);

        matchData.removePlayer(playerId);
        // (뭔가 바뀌는 값이 있어야 콜백이 도므로 나중에 리셋해줄 attackingPlayer 인덱스값을 임의로 설정합니다. 이거 지우면 큰일나요!!)
        matchData.setAttackerPlayerIdx(matchData.getPlayers().size());

        // 플레이어 퇴장 이벤트 발행
        // Handle match leave event.
        gameEventsRepository.sendMatchPlayerLeaveEvent(matchId, playerId);
        data.unsubscribeToPlayerDataUpdates(playerId);

        // 내부 DB 갱신
        // Update internal DB & Firebase RDB to reflect this change.
        var newMatchData = gameDataRepository.saveMatch(matchData);
        gameDataRepository.updateFrdbMatchData(newMatchData);
        return true;
    }

    private void setupRunningMatch(UUID matchId) {
        System.out.printf("[MATCH] MatchRunnerService::setupMatch | (%s)\n", matchId);
        var data = new RunningMatch();

        // 매치 데이터 설정
        // 데이터 가져오기 등 Delegate 함수 연결
        data.setMatchDataDelegateMethod(new RunningMatch.MatchDataDelegateMethod() {
            @Override
            public ScheduledFuture<?> setTimerAndRun(UUID matchId, int seconds, Runnable callback) {
                var matchData = gameDataRepository.findMatchById(matchId).orElse(null);

                if (matchData == null) {
                    System.err.printf("[MATCH] MatchRunnerService::setTimer(MATCH: %s) | MATCH DOES NOT EXIST IN DB!\n", matchId);
                    return null;
                }

                ZonedDateTime currentTime = ZonedDateTime.now(TIME_ZONE_ID);
                ZonedDateTime endTime = currentTime.plusSeconds(seconds);

                var timerHandle = scheduler.schedule(callback, endTime.toInstant());
                matchData.setCountdownStartTime(currentTime);
                matchData.setCountdownSec(seconds);

                gameDataRepository.saveMatch(matchData);
                gameDataRepository.updateFrdbMatchData(matchData);

                System.out.printf(
                        "[MATCH] MatchRunnerService::setTimer(MATCH: %s (%s), SECS: %d, %s -> %s)\n",
                        matchId,
                        matchData.getState(),
                        seconds,
                        FrdbConstants.TIME_FORMATTER.format(currentTime),
                        FrdbConstants.TIME_FORMATTER.format(endTime)
                );
                return timerHandle;
            }

            @Override
            public Optional<MatchData> getMatchData(UUID id) {
                return gameDataRepository.findMatchById(id);
            }

            @Override
            public Optional<PlayerData> getPlayerData(UUID id) {
                return gameDataRepository.findPlayerById(id);
            }

            @Override
            public List<PERK> getAvailablePerks(List<PERK> ownedPerks) {
                return getUnownedPerks(ownedPerks);
            }

            @Override
            public void commitMatchData(MatchData data) {
                mutex.lock();
                try {
                    gameDataRepository.saveMatch(data);
                    // gameDataRepository.updateFrdbMatchData(matchData);
                } finally {
                    mutex.unlock();
                }
            }

            @Override
            public void commitPlayerData(PlayerData data) {
                mutex.lock();
                try {
                    gameDataRepository.savePlayer(data);
                    // gameDataRepository.findMatchById(playerData.getJoinedMatchId()).ifPresent(gameDataRepository::updateFrdbMatchData);
                } finally {
                    mutex.unlock();
                }
            }

            @Override
            public void commitFrdbData(MatchData data) {
                mutex.lock();
                try {
                    gameDataRepository.updateFrdbMatchData(data);
                } finally {
                    mutex.unlock();
                }
            }

            @Override
            public void updateMatchDataForRoundBegin(MatchData matchData) {
                matchTurnCalcService.updateMatchDataForRoundBegin(matchData);
            }

            @Override
            public void updateMatchDataForTurnBegin(MatchData matchData) {
                matchTurnCalcService.updateMatchDataForTurnBegin(matchData);
            }

            @Override
            public void calculateTurn(MatchData matchData) {
                matchTurnCalcService.calculateTurn(matchData);
            }

            @Override
            public void receiveItemForAllPlayers(MatchData matchData) {
                itemService.giveRandomItem(matchData);
            }
        });

        // 플레이어 입장 등 매치 단위 옵저버 연결
        // Connect observers.
        data.connectMatch(matchId);
        data.subscribeToMatchPlayerJoinEvents(gameEventsRepository.getMatchPlayerJoinEventsObservable(matchId));
        data.subscribeToMatchPlayerLeaveEvents(gameEventsRepository.getMatchPlayerLeaveEventsObservable(matchId));
        data.subscribeToMatchDataUpdates(matchId, gameEventsRepository.getMatchDataObservable(matchId));

        allRunningMatches.put(matchId, data);
    }

    private void freeRunningMatch(UUID matchId) {
        System.out.printf("[MATCH] MatchRunnerService::freeMatch | (%s)\n", matchId);
        var data = allRunningMatches.getOrDefault(matchId, null);

        // 연결된 옵저버 연결 해제
        if (data != null) {
            System.out.printf("[MATCH] MatchRunnerService::freeMatch | DISCONNECTING PREVIOUSLY CONNECTED MATCH! (%s)\n", matchId);
            data.disconnectMatch();
        }

        allRunningMatches.remove(matchId);
    }

    private List<PERK> getUnownedPerks(List<PERK> ownedPerks) {
        return Arrays.stream(PERK.values())
                .filter(perk -> !ownedPerks.contains(perk))
                .toList();
    }
}