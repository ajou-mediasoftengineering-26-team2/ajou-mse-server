package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;

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

    private final TaskScheduler scheduler;
    private final Map<UUID, ScheduledFuture<?>> countdownSchedulers;

    public MatchService(FrdbService frdbService, MatchDataRepository matchDataRepository, PlayerDataRepository playerDataRepository) {
        this.frdbService = frdbService;
        this.matchDataRepository = matchDataRepository;
        this.playerDataRepository = playerDataRepository;

        this.scheduler = new SimpleAsyncTaskScheduler();
        this.countdownSchedulers = new HashMap<>();
    }

    /**
     * 참가 가능한 아무 매치를 반환합니다.
     *
     * @return 참가 가능한 매치.
     */
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
    public MatchData createMatch() {
        // DB에 저장
        MatchData matchData = new MatchData();
        // TODO: StationRepository에서 현재 역 가져오기
        String station = "CITY_HALL";
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
        newMatchData.updatePlayer(playerData.get());
        newPlayerData.setJoinedMatchId(matchId);

        // 로비 상태 변경
        onPlayerJoin(newMatchData);

        // 내부 DB속 로비, 플레이어 데이터 갱신
        newPlayerData = playerDataRepository.save(newPlayerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // (TODO: 매치 시작시)
        PlayerData first = newMatchData.getPlayers().getFirst();

        first.setAttacking(true);
        first.setSelecting(true);
        playerDataRepository.save(first);

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

    private void onPlayerJoin(MatchData matchData) {
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

    private void onPlayerLeave(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        MATCH_STATE state = matchData.getState();

        if (state == MATCH_STATE.LOBBY_START_COUNTDOWN && !players.isEmpty()) {
            matchData.setState(MATCH_STATE.LOBBY_WAITING);
            cancelCountdownForMatch(matchData);
        } else if (state.isIngame()) {
            matchData.setState(MATCH_STATE.END_PLAYER_DISCONNECTED);
        }
        //matchData.setCountdownStartTime(ZonedDateTime.now());
    }

    private void onMatchStart(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        List<PlayerData> players = matchData.getPlayers();
        System.out.println("Players: " + players);

        matchData.setState(MATCH_STATE.GAME_ATK_CHOICE);

        // 내부 DB속 로비, 플레이어 데이터 갱신
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);

            if (i == 0) {
                // 첫 플레이어부터 공격
                player.setAttacking(true);
                player.setSelecting(true);
            } else {
                player.setAttacking(false);
                player.setSelecting(false);
            }
        }

        // 다음 턴 제한시간 설정
        if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
            System.err.println("FAILED TO SCHEDULE INITIAL TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
        }

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(matchData.getId(), matchData);
    }

    private void onMatchTurn(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        List<PlayerData> players = matchData.getPlayers();
        MATCH_STATE state = matchData.getState();
        int playerIdx = matchData.getCurrentPlayerIdx();
        PlayerData currentPlayer = players.get(playerIdx);
        boolean isAttackSuccess = true;
        boolean isPlayerKO = false;

        // TODO: 더 자세한 로직 구현
        switch (state) {
            case GAME_ATK_CHOICE:
                // 공격 제한시간 끝
                // (FIXME) 우선은 공격 성공으로 간주하고 피 뽑기 + 다음 플레이어로 턴 넘기기
                if (isAttackSuccess) {
                    for (PlayerData player : players) {
                        if (!player.isAttacking()) {
                            player.setHp(Math.max(0, player.getHp() - 2));
                            isPlayerKO = (player.getHp() <= 0);
                        }
                    }
                }

                if (isPlayerKO) {
                    // KO?
                    currentPlayer.setWins(currentPlayer.getWins() + 1);
                    matchData.setState(MATCH_STATE.GAME_ROUND_END_PLAYER_KO);
                } else {
                    // 다음 턴
                    playerIdx = (playerIdx + players.size() + 1) % players.size();
                    matchData.setState(MATCH_STATE.GAME_DEF_CHOICE);
                }
                break;
            // case GAME_DEF_CHOICE:
            default:
                // 방어 제한시간 끝
                // (FIXME) 우선은 다음 플레이어로 턴 넘기기
                playerIdx = (playerIdx + players.size() + 1) % players.size();
                matchData.setCurrentTurn(matchData.getCurrentTurn() + 1);
                matchData.setState(MATCH_STATE.GAME_ATK_CHOICE);
                break;
        }

        matchData.setCurrentPlayerIdx(playerIdx);
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);

            if (i == playerIdx) {
                player.setSelecting(true);
            } else {
                player.setSelecting(false);
            }
        }
        playerDataRepository.saveAll(players);

        if (isPlayerKO) {
            // (FIXME) 우선은 게임 끝...
            int winnerPlayerIdx = -1,
                winsMax = -1;

            for (int i = 0; i < players.size(); i++) {
                PlayerData player = players.get(i);

                if (player.getWins() > winsMax) {
                    winnerPlayerIdx = i;
                    winsMax = player.getWins();
                }
            }

            matchData.setWinnerPlayerIdx(winnerPlayerIdx);
            matchData.setCurrentRound(matchData.getCurrentRound() + 1);
            matchData.setCurrentTurn(0);
            matchData.setState(MATCH_STATE.END_RESULT);
        }

        // 다음 턴 제한시간 설정
        if (matchData.getState().isIngame()) {
            if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
                System.err.println("FAILED TO SCHEDULE MATCH TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
            }
        }

        // 내부 DB속 로비, 플레이어 데이터 갱신
        matchDataRepository.save(matchData);

        // Firebase RDB에 수정사항 갱신
        frdbService.setMatch(matchData.getId(), matchData);
    }

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

    private boolean cancelCountdownForMatch(MatchData matchData) {
        ScheduledFuture<?> handle = countdownSchedulers.getOrDefault(matchData.getId(), null);

        if (handle != null) {
            handle.cancel(false);
            return true;
        }

        return false;
    }
}
