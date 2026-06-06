package team2.mse.ajou.server.domain.shared.match.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.subway.repository.StationRepository;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * Match data management service.
 *
 * @author Ahn Yubin / 202021088
 * @author Junseo Hwang 202322128
 */
public class MatchServiceLegacy {
    private final FrdbService frdbService;
    private final MatchDataRepository matchDataRepository;
    private final PlayerDataRepository playerDataRepository;
    private final StationRepository stationRepository;

    private final MatchTurnCalcService matchTurnCalcService;

    // Handles of scheduled tasks for each match so that we can cancel any tasks. Mostly for countdown timers.
    private final TaskScheduler scheduler;
    private final Map<UUID, ScheduledFuture<?>> countdownSchedulers;

    private final Random attackerRandom;

    public MatchServiceLegacy(FrdbService frdbService, MatchDataRepository matchDataRepository, PlayerDataRepository playerDataRepository, StationRepository stationRepository, MatchTurnCalcService matchTurnCalcService) {
        this.frdbService = frdbService;
        this.matchDataRepository = matchDataRepository;
        this.playerDataRepository = playerDataRepository;
        this.stationRepository = stationRepository;
        this.matchTurnCalcService = matchTurnCalcService;

        this.scheduler = new SimpleAsyncTaskScheduler();
        this.countdownSchedulers = new HashMap<>();

        this.attackerRandom = new Random(System.currentTimeMillis());
    }

    /**
     * Returns any match/lobbies available for joining.
     *
     * @return Match. null if not found.
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
     * Creates a new match.
     *
     * @return Match data.
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
     * Fetches match data of given player UUID.
     *
     * @param playerId Player UUID.
     * @return Match data. null if not found.
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
     * Initializes given `MatchData` for the first turn of given round.
     *
     * @param matchData Match data to be modified.
     */
    public void initializeMatchRound(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        if (players.size() < 2) {
            throw new ApiError(5005, "Insufficient players in the match!");
        }

        // Random player attacks
        int attackerIdx = attackerRandom.nextInt(0, players.size());

        // Reset HP and turn state. (Both players!!)
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            player.setHp(10);
            player.setFinalWinner(false);
            player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            player.setAckState(team2.mse.ajou.server.domain.shared.ack.ACK_TYPE.NO_ACK);
            player.setSelecting(true);
            player.setAttacking(i == attackerIdx);
        }

        matchData.setAttackerPlayerIdx(attackerIdx);
        matchData.setAttackSuccess(false);

        // DamageList를 초기에 설정해야할지도 모르겠습니다.
        matchData.clearDamageDataList();
        matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);
    }

    /**
     * Joins player in match.
     *
     * @param playerId Player UUID.
     * @param matchId  Match UUID.
     * @return Whether if joining was successful.
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

        // If game has already started, abort.
        if (newMatchData.getState().isIngame()) {
            return false;
        }

        System.out.println("MATCH JOIN: %s / %s".formatted(newMatchData.getId(), newMatchData.getPlayers()));

        // Add player to the list of joined player for match, and set joined match for player.
        newPlayerData.setJoinedMatchId(matchId);
        newMatchData.updatePlayer(newPlayerData);

        // Handle match join event.
        onPlayerJoin(newMatchData);

        // Update internal DB to reflect this change.
        // newPlayerData = playerDataRepository.save(newPlayerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // Apply to Firebase RDB aswell.
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

        // Remove player from match.
        newPlayerData.setJoinedMatchId(null);
        newPlayerData.setReady(false);
        newMatchData.removePlayer(playerData.get().getId());
        /*if (!newMatchData.removePlayer(playerData.get().getId())) {
            return false;
        }*/

        // Handle match leave event.
        onPlayerLeave(newMatchData);

        // Update internal DB to reflect this change.
        newPlayerData = playerDataRepository.save(newPlayerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // Apply to Firebase RDB aswell.
        frdbService.setMatch(matchId, newMatchData);
        return true;
    }

    /**
     * Fetches player data by UUID.
     *
     * @param id UUID.
     * @return Player data. null if not found.
     */
    public PlayerData getPlayerById(UUID id) {
        return playerDataRepository
                .findById(id)
                .orElse(null);
    }

    /**
     * Saves player data to Firebase Realtime DB.
     *
     * @param playerData Player data.
     */
    @Transactional
    public void savePlayer(PlayerData playerData) {
        Optional<MatchData> matchData = matchDataRepository.findById(playerData.getJoinedMatchId());
        PlayerData newPlayerData = playerDataRepository.save(playerData);

        // We need match data because we have to update player information inside the match.
        if (matchData.isEmpty()) {
            return;
        }

        MatchData newMatchData = matchData.get();

        // Update internal DB to reflect this change.
        newMatchData.updatePlayer(playerData);
        newMatchData = matchDataRepository.save(newMatchData);

        // Apply to Firebase RDB aswell.
        frdbService.setMatch(newMatchData.getId(), newMatchData);
    }

    /**
     * Callback called when player joins the match. Sets up timer to start a match if sufficient players have joined and are ready.
     *
     * @param matchData Match data to be modified.
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
     * Callback called when player leaves the match. Cancels timer if there's one.
     *
     * @param matchData Match data to be modified.
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
        // matchData.setCountdownStartTime(ZonedDateTime.now());
    }

    /**
     * Callback called when match is started. Determines the initial attacker, and sets the timer to get the inputs for the first turn.
     *
     * @param matchId Match ID.
     */
    @Transactional
    public void onMatchStart(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        initializeMatchNew(matchData);

        // Schedule turn handling task in 5 seconds from now.
        if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
            System.err.println("FAILED TO SCHEDULE INITIAL TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
        }

        // Update internal DB to reflect this change.
        List<PlayerData> newPlayerDatas = playerDataRepository.saveAll(matchData.getPlayers());
        matchDataRepository.save(matchData);

        // Apply to Firebase RDB aswell.
        frdbService.setMatch(matchData.getId(), matchData);
    }

    /**
     * Callback called when a turn of match needs to be processed. Handles player input and attack/defence and its outcomes.
     *
     * @param matchId Match ID.
     */
    @Transactional
    public void onMatchTurn(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        // 5초 타이머는 choice -> finished로 갈때만 사용합니다.
        // 나중에 ack 오류 처리를 위해 finished -> choice 타이머를 가동할 수 도 있습니다.
        if (matchData.getState() != MATCH_STATE.GAME_PLAYER_CHOICE) return;
        // Schedule turn handling task in 5 seconds from now.
//        if (matchData.getState().isIngame()) {
//            if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
//                System.err.println("FAILED TO SCHEDULE MATCH TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
//            }
//        }

        matchTurnCalcService.calculateTurn(matchData);

        // Update internal DB to reflect this change.
        List<PlayerData> newPlayerDatas = playerDataRepository.saveAll(matchData.getPlayers());
        MatchData newMatchData = matchDataRepository.save(matchData);

        // Apply to Firebase RDB aswell.
        frdbService.setMatch(newMatchData.getId(), newMatchData);
    }

    /**
     * Starts the next 5-sec choice after both clients finish animation and send Ack.
     *
     * @param matchId Match ID.
     */
    @Transactional
    public void startNextTurn(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId).orElse(null);
        if (matchData == null) {
            System.err.println("MATCH `" + matchId + "` NOT FOUND!");
            return;
        }

        if (matchData.getState() == MATCH_STATE.END_RESULT || matchData.getState() == MATCH_STATE.END_PLAYER_DISCONNECTED) {
            frdbService.setMatch(matchData.getId(), matchData);
            return;
        }

        int attackerIdx = matchData.getAttackerPlayerIdx();
        List<PlayerData> players = matchData.getPlayers();

        // 초기화를 안해도 될 것 같긴함
        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            player.setChoice(HAND_CHOICE.SHAKE_OVER_HANDS);
            player.setAckState(ACK_TYPE.NO_ACK);
            player.setSelecting(true);
            player.setAttacking(i == attackerIdx);

            if(matchData.getCurrentTurn() == 0){
                player.getStatusEffectList().clear();
            }
        }

        matchData.setState(MATCH_STATE.GAME_PLAYER_CHOICE);
        matchData.clearDamageDataList();
        matchData.setAttackSuccess(false);
        matchData.setKo(false);

        if (!setCountdownForMatch(matchData, () -> onMatchTurn(matchData.getId()), 5)) {
            System.err.println("FAILED TO SCHEDULE NEXT TURN COUNTDOWN FOR GAME `" + matchData.getId() + "`");
        }

        playerDataRepository.saveAll(players);
        MatchData newMatchData = matchDataRepository.save(matchData);
        frdbService.setMatch(newMatchData.getId(), newMatchData);
    }

    /**
     * Closes all match and removes all data.
     */
    public void deleteAllMatch() {
        for (ScheduledFuture<?> handler : countdownSchedulers.values()) {
            handler.cancel(false);
        }
        countdownSchedulers.clear();
        matchDataRepository.deleteAll();
        frdbService.clearAllMatch();
    }

    /**
     * Sets up countdown timer, running `Runnable` callback once the countdown reaches zero.
     * Also modifies given `MatchData` to be used for DB updates and such.
     *
     * @param matchData Match data to be modified.
     * @param callback  Callback on countdown end.
     * @param seconds   Countdown seconds.
     * @return Whether it was successful.
     */
    public boolean setCountdownForMatch(MatchData matchData, Runnable callback, int seconds) {
        ScheduledFuture<?> handlePrev = countdownSchedulers.getOrDefault(matchData.getId(), null);

        if (handlePrev != null && !handlePrev.isDone()) {
            return false;
        }

        ZonedDateTime currentTime = ZonedDateTime.now(),
                when = currentTime.plusSeconds(seconds);

        System.out.println("COUNTDOWN MATCH " + matchData.getId() + " @ " + currentTime);

        ScheduledFuture<?> handle = scheduler.schedule(callback, when.toInstant());
        matchData.setCountdownStartTime(currentTime);
        matchData.setCountdownSec(seconds);

        countdownSchedulers.put(matchData.getId(), handle);
        return true;
    }

    /**
     * Sets up countdown timer, running `Runnable` callback once the countdown reaches zero.
     * Also modifies given `MatchData` to be used for DB updates and such.
     *
     * @param matchData Match data to be modified.
     * @param callback  Callback on countdown end.
     * @param when      Countdown end time.
     * @return Whether it was successful.
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
     * Cancels given match's timer.
     *
     * @param matchData Match data to be modified.
     * @return Whether it was successful.
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

    /**
     * Initializes given `MatchData` for new match.
     *
     * @param matchData Match data to be modified.
     */
    private void initializeMatchNew(MatchData matchData) {
        initializeMatchRound(matchData);

        // Reset wins
        List<PlayerData> players = matchData.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            PlayerData player = players.get(i);
            player.setWins(0);
        }
    }
}
