package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.subway.repository.StationRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * 게임 매치 정보 관리 서비스.
 *
 * @author yubin
 */
@Service
public class MatchService {
    private final FrdbService frdbService;
    private final MatchDataRepository matchDataRepository;
    private final PlayerDataRepository playerDataRepository;
    private final StationRepository stationRepository;

    private final MatchTurnCalcService matchTurnCalcService;

    // 각 로비마다 카운트다운 핸들(?)을 담는 map
    private final TaskScheduler scheduler;
    private final Map<UUID, ScheduledFuture<?>> countdownSchedulers;

    public MatchService(FrdbService frdbService, MatchDataRepository matchDataRepository, PlayerDataRepository playerDataRepository, StationRepository stationRepository, MatchTurnCalcService matchTurnCalcService) {
        this.frdbService = frdbService;
        this.matchDataRepository = matchDataRepository;
        this.playerDataRepository = playerDataRepository;
        this.stationRepository = stationRepository;
        this.matchTurnCalcService = matchTurnCalcService;

        this.scheduler = new SimpleAsyncTaskScheduler();
        this.countdownSchedulers = new HashMap<>();
    }

    /**
     * 참가 가능한 아무 매치를 반환합니다.
     *
     * @return 참가 가능한 매치.
     */
    @Transactional
    public MatchData getOpenMatch() {
        return matchDataRepository.findAll()
                .stream()
                .filter(match -> match.getPlayers().size() < 2 && match.getState() == MATCH_STATE.LOBBY_WAITING)
                .findFirst()
                .orElse(null);
    }

    /**
     * 신규 매치를 생성합니다.
     *
     * @return 매치 정보.
     */
    @Transactional
    public MatchData createMatch() {
        // DB에 저장
        MatchData matchData = new MatchData();

        String station = stationRepository.getStation();
        matchData.setStation(station);

        matchData = matchDataRepository.save(matchData);

        System.out.println("MATCH CREATE: %s / %s".formatted(matchData.getId(), matchData.getPlayers()));

        return matchData;
    }

    /**
     * 플레이어 UUID로 플레이어가 속한 매치 정보를 가져옵니다.
     *
     * @param playerId 플레이어 UUID.
     * @return 매치 정보. 없을 경우 null.
     */
    @Transactional
    public MatchData findMatchByPlayerId(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId).orElse(null);
        if (playerData == null) {
            return null;
        }

        return matchDataRepository.findById(playerData.getJoinedMatchId()).orElse(null);
    }

    /**
     * 플레이어를 매치에 참가시킵니다.
     *
     * @param playerId 플레이어 UUID.
     * @param matchId  매치 UUID.
     * @return 참가 여부.
     */
    @Transactional
    public boolean joinMatch(UUID playerId, UUID matchId) {
        Optional<MatchData> matchData = matchDataRepository.findById(matchId);
        Optional<PlayerData> playerData = playerDataRepository.findById(playerId);
        if (matchData.isEmpty() || playerData.isEmpty()) {
            return false;
        }

        MatchData newMatchData = matchData.get();
        PlayerData newPlayerData = playerData.get();

        // 이미 게임이 진행중이면 참가 불가능
        if (newMatchData.getState().isIngame()) {
            return false;
        }

        System.out.println("MATCH JOIN: %s / %s".formatted(newMatchData.getId(), newMatchData.getPlayers()));

        // 로비에 플레이어 추가, 플레이어에는 참가한 로비 값 갱신
        newPlayerData.setJoinedMatchId(matchId);
        newMatchData.updatePlayer(newPlayerData);

        // 로비 상태 변경
        onPlayerJoin(newMatchData);

        // 내부 DB속 로비, 플레이어 데이터 갱신
        // newPlayerData = playerDataRepository.save(newPlayerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(matchId, newMatchData);
        return true;
    }

    /**
     * 플레이어를 매치에서 퇴장시킵니다.
     *
     * @param playerId 플레이어 UUID.
     * @param matchId  매치 UUID.
     * @return 퇴장 여부.
     */
    @Transactional
    public boolean leaveMatch(UUID playerId, UUID matchId) {
        if (playerId == null || matchId == null) {
            return false;
        }

        Optional<MatchData> matchData = matchDataRepository.findById(matchId);
        Optional<PlayerData> playerData = playerDataRepository.findById(playerId);
        if (matchData.isEmpty() || playerData.isEmpty()) {
            return false;
        }

        MatchData newMatchData = matchData.get();
        PlayerData newPlayerData = playerData.get();

        // 로비로부터 플레이어 제거
        newPlayerData.setJoinedMatchId(null);
        newPlayerData.setReady(false);
        newMatchData.removePlayer(playerData.get().getId());
        /*if (!newMatchData.removePlayer(playerData.get().getId())) {
            return false;
        }*/

        // 로비 상태 변경
        onPlayerLeave(newMatchData);

        // 내부 DB속 로비, 플레이어 데이터 갱신
        newPlayerData = playerDataRepository.save(newPlayerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(matchId, newMatchData);
        return true;
    }

    /**
     * 플레이어 정보 수정을 위해 값을 가져옵니다.
     *
     * @param id UUID.
     * @return 플레이어 정보. 찾지 못할 경우 null.
     */
    public PlayerData getPlayerById(UUID id) {
        return playerDataRepository
                .findById(id)
                .orElse(null);
    }

    /**
     * 수정한 플레이어 정보를 Firebase + 내부 DB에 저장합니다.
     *
     * @param playerData 플레이어 정보.
     */
    @Transactional
    public void savePlayer(PlayerData playerData) {
        Optional<MatchData> matchData = matchDataRepository.findById(playerData.getJoinedMatchId());
        PlayerData newPlayerData = playerDataRepository.save(playerData);

        // 로비상의 플레이어 정보도 갱신
        if (matchData.isEmpty()) {
            return;
        }

        MatchData newMatchData = matchData.get();

        // 내부 DB속 로비, 플레이어 데이터 갱신
        newMatchData.updatePlayer(playerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(newMatchData.getId(), newMatchData);
    }

    /**
     * 플레이어 참가시 데이터 수정용 콜백. 충분한 인원이 참여했을 경우 매치 시작 타이머를 설정합니다.
     * @param matchData 매치 데이터.
     */
    @Transactional
    public void onPlayerJoin(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        MATCH_STATE state = matchData.getState();
        boolean isAllReady = players.stream().allMatch(PlayerData::isReady);
        int playerSize = players.size();

        // 2인 이상 플레이어가 접속했는지 + 모두 ready 상태인지 확인
        if (playerSize >= 2 && isAllReady && state == MATCH_STATE.LOBBY_WAITING) {
            // 게임 시작!!
            matchData.setState(MATCH_STATE.LOBBY_START_COUNTDOWN);
            setCountdownForMatch(matchData, () -> onMatchStart(matchData.getId()), 5);
        } else {
            // matchData.setState(MATCH_STATE.LOBBY_START_COUNTDOWN);
            // NO-OP
        }
    }

    /**
     * 플레이어 퇴장시 데이터 수정용 콜백. 충분한 인원이 참여했을 경우 매치 시작 타이머를 설정합니다.
     * @param matchData 매치 데이터.
     */
    @Transactional
    public void onPlayerLeave(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        MATCH_STATE state = matchData.getState();

        if (state == MATCH_STATE.LOBBY_START_COUNTDOWN && !players.isEmpty()) {
            System.out.println("LOBBY PLR LEFT: CANCEL START COUNTDOWN FOR `" + matchData.getId() + "`");
            matchData.setState(MATCH_STATE.LOBBY_WAITING);
            cancelCountdownForMatch(matchData);
        } else if (state.isIngame()) {
            System.out.println("LOBBY PLR LEFT: FORCE END GAME FOR `" + matchData.getId() + "`");
            matchData.setState(MATCH_STATE.END_PLAYER_DISCONNECTED);
            cancelCountdownForMatch(matchData);
        }
        //matchData.setCountdownStartTime(ZonedDateTime.now());
    }

    /**
     * 매치 턴 시작시 콜백. 공격수를 정하고 첫 턴 입력을 받도록 타이머를 설정합니다.
     * @param matchId 매치 ID.
     */
    @Transactional
    public void onMatchStart(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        matchTurnCalcService.initializeMatch(matchData);

        // 다음 턴 제한시간 설정
        if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
            System.err.println("FAILED TO SCHEDULE INITIAL TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
        }

        // 내부 DB속 로비, 플레이어 데이터 갱신
        List<PlayerData> newPlayerDatas = playerDataRepository.saveAll(matchData.getPlayers());
        matchDataRepository.save(matchData);

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(matchData.getId(), matchData);
    }

    /**
     * 매치 턴 진행시 콜백. 여기서 (미리 API로 받은) 플레이어 입력 처리 및 로직을 처리하면 되겠습니다.
     * @param matchId 매치 ID.
     */
    @Transactional
    public void onMatchTurn(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        matchTurnCalcService.calculateTurn(matchData);

        // 다음 턴 제한시간 설정
        if (matchData.getState().isIngame()) {
            if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
                System.err.println("FAILED TO SCHEDULE MATCH TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
            }
        }

        // 내부 DB속 로비, 플레이어 데이터 갱신
        List<PlayerData> newPlayerDatas = playerDataRepository.saveAll(matchData.getPlayers());
        MatchData newMatchData = matchDataRepository.save(matchData);

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(newMatchData.getId(), newMatchData);
    }

    /**
     * 모든 매치를 닫고 정리합니다.
     */
    public void deleteAllMatch() {
        for (ScheduledFuture<?> handler: countdownSchedulers.values()) {
            handler.cancel(false);
        }
        countdownSchedulers.clear();
        matchDataRepository.deleteAll();
        frdbService.clearAllMatch();
    }

    /**
     * 주어진 매치에 대해 카운트다운 설정. 현재 시각 기준 주어진 초가 지나면 Runnable 형의 콜백 함수가 실행됩니다.
     * 또, 주어진 MatchData 인스턴스의 타이머 관련 필드 값을 수정해 FRDB 반영에도 사용할 수 있게 해줍니다.
     *
     * @param matchData 매치 정보. 해당 인스턴스의 값이 수정됩니다.
     * @param callback 콜백 함수.
     * @param seconds 초.
     * @return 성공 여부. 이미 해당 매치에 타이머가 설정되고 실행이 아직 되지 않은 경우.
     */
    private boolean setCountdownForMatch(MatchData matchData, Runnable callback, int seconds) {
        ScheduledFuture<?> handlePrev = countdownSchedulers.getOrDefault(matchData.getId(), null);

        if (handlePrev != null && !handlePrev.isDone()) {
            return false;
        }

        ZonedDateTime   currentTime = ZonedDateTime.now(),
                        when = currentTime.plusSeconds(seconds);

        System.out.println("COUNTDOWN MATCH " + matchData.getId() + " @ " + currentTime);

        ScheduledFuture<?> handle = scheduler.schedule(callback, when.toInstant());
        matchData.setCountdownStartTime(currentTime);
        matchData.setCountdownSec(seconds);

        countdownSchedulers.put(matchData.getId(), handle);
        return true;
    }

    /**
     * 주어진 매치에 대해 카운트다운 설정. 주어진 시각에 도달하면 Runnable 형의 콜백 함수가 실행됩니다.
     * 또, 주어진 MatchData 인스턴스의 타이머 관련 필드 값을 수정해 FRDB 반영에도 사용할 수 있게 해줍니다.
     *
     * @param matchData 매치 정보. 해당 인스턴스의 값이 수정됩니다.
     * @param callback 콜백 함수.
     * @param when 콜백 함수 실행 시각.
     * @return 성공 여부. 이미 해당 매치에 타이머가 설정되고 실행이 아직 되지 않은 경우.
     */
    private boolean setCountdownForMatch(MatchData matchData, Runnable callback, ZonedDateTime when) {
        ScheduledFuture<?> handlePrev = countdownSchedulers.getOrDefault(matchData.getId(), null);

        if (handlePrev != null && !handlePrev.isDone()) {
            return false;
        }

        ZonedDateTime currentTime = ZonedDateTime.now();
        int seconds = (int) currentTime.until(when, ChronoUnit.SECONDS);

        System.out.println("COUNTDOWN MATCH " + matchData.getId() + " @ " + currentTime);

        ScheduledFuture<?> handle = scheduler.schedule(callback, when.toInstant());
        matchData.setCountdownStartTime(currentTime);
        matchData.setCountdownSec(seconds);

        countdownSchedulers.put(matchData.getId(), handle);
        return true;
    }

    /**
     * 주어진 매치의 타이머를 취소시킵니다.
     * @param matchData
     * @return
     */
    private boolean cancelCountdownForMatch(MatchData matchData) {
        ScheduledFuture<?> handle = countdownSchedulers.getOrDefault(matchData.getId(), null);

        if (handle != null) {
            handle.cancel(false);
            countdownSchedulers.remove(matchData.getId());
            return true;
        }

        return false;
    }
}
