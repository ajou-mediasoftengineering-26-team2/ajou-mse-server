package team2.mse.ajou.server.domain.round.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.repository.GameDataRepository;

import java.util.UUID;

/**
 * @author Junseo Hwang 202322128
 */
@Service
public class RoundService implements IRoundService {
    private final GameDataRepository gameDataRepository;

    public RoundService(GameDataRepository gameDataRepository) {
        this.gameDataRepository = gameDataRepository;
    }

    @Override
    public void receiveRoundStart(UUID playerId) {
        gameDataRepository.findPlayerById(playerId).ifPresentOrElse(playerData -> {
            playerData.setAckState(ACK_TYPE.ROUND_START_ANIMATION_END);
            gameDataRepository.savePlayer(playerData);
        }, () -> {
            throw new IllegalArgumentException("Not Found: " + playerId);
        });
        /*
        PlayerData playerData = playerDataJpaRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));

        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ROUND_START_ANIMATION_END);
        matchData.updatePlayer(playerData);

        playerDataJpaRepository.save(playerData);
        matchDataJPARepository.save(matchData);

        System.out.println(playerId + ": round start-ack");

        // 라운드 시작 애니메이션 종료 -> 플레이어 공격 선택
        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ROUND_START_ANIMATION_END)) {
            matchData.setState(MATCH_STATE.GAME_PLAYER_CHOICE);
            MatchData updMatchData = matchDataJPARepository.save(matchData);

            frdbRepository.setMatch(updMatchData.getId(), updMatchData);

            matchService.startNextTurn(matchData.getId());
        }
        */
    }

    @Override
    public void receiveRoundEnd(UUID playerId) {
        var playerData = gameDataRepository.findPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player Not Found: " + playerId));
        UUID matchId = playerData.getJoinedMatchId();
        var matchData = gameDataRepository.findMatchById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + matchId));

        if (matchData.getState() != MATCH_STATE.GAME_ROUND_END_PLAYER_KO) {
            throw new IllegalStateException("Round end animation ACK can be submitted only after player KO!");
        }

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        playerData.setAckState(ACK_TYPE.ROUND_END_ANIMATION_END);
        gameDataRepository.savePlayer(playerData);

        /*
        PlayerData playerData = playerDataJpaRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));
        UUID matchId = playerData.getJoinedMatchId();
        MatchData matchData = matchDataJPARepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + matchId));

        playerData.setAckState(ACK_TYPE.ROUND_END_ANIMATION_END);
        matchData.updatePlayer(playerData);

        playerDataJpaRepository.save(playerData);
        matchDataJPARepository.save(matchData);

        System.out.println(playerId + ": round end-ack");

        // 라운드 종료 애니메이션 종료(플레이어 ko)
        // -> 1라운드면 elemental 선택
        // -> 2라운드 이상이면 perk 선택
        if (ackService.isAllAckReceived(matchData, ACK_TYPE.ROUND_END_ANIMATION_END)) {
            boolean isElementalChoice = matchData.getCurrentRound() == 1;

            if (isElementalChoice) {
                matchData.setState(MATCH_STATE.GAME_ELEMENTAL_CHOICE);
            } else {
                // perk 선택의 경우, 두 플레이어 모두 선택지 리스트 생성
                matchData.setState(MATCH_STATE.GAME_PERK_CHOICE);
                List<PlayerData> players = matchData.getPlayers();

                // -> 1] 선택 가능한 모든 perk 목록 불러오기
                // -> 2] 그 중 (최대) 3개의 랜덤한 것을 전달하기
                for (PlayerData player : players) {
                    List<PERK> availablePerks = new ArrayList<>(perkService.getUnownedPerks(player));
                    int returnSz = Math.min(availablePerks.size(), 3);

                    Collections.shuffle(availablePerks);
                    player.setPerkChoiceCurrent(null);
                    player.setPerkChoiceList(availablePerks.subList(0, returnSz));
                }

                // (DB 추가 갱신)
                playerDataJpaRepository.saveAll(players);
            }

            // 10초가 지나면 서버는 perk item receiving / elemental receiving 상태가 되도록 타이머 ON
            MATCH_STATE nextState = isElementalChoice ? MATCH_STATE.GAME_ELEMENTAL_RECEIVING : MATCH_STATE.GAME_PERK_ITEM_RECEIVING;

            matchService.setCountdownForMatch(matchData, () -> {
                System.out.printf("Countdown END for match `%s`\n", matchId);

                MatchData countdownMatchData = matchDataJPARepository.findById(matchId)
                        .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + matchId));

                // "이때 perk(elemental)과 item이 다 업데이트 됨"
                // (item: 랜덤 아이템 지급)
                if(nextState == MATCH_STATE.GAME_PERK_ITEM_RECEIVING){
                    itemService.giveRandomItem(countdownMatchData);
                }

                // (perk: perkChoiceCurrent값에 해당하는 perk 지급 & perkChoiceList 빈 리스트로 갱신)
                List<PlayerData> players = countdownMatchData.getPlayers();
                for (PlayerData player : players) {
                    // 여기서 perk을 바꾸면 안됩니다!!!
                    // 기본적으로 perk을 null로 설정하기 때문에
                    // perk을 선택하지 않는 elemental에서 null이 list에 들어가버립니다!!!!!!!!!!!!
//                    List<PERK> perks = player.getPerkList();
//                    PERK selectedPerk = player.getPerkChoiceCurrent();
//
//                    perks.add(selectedPerk);
//                    player.setPerkList(perks);
//                    player.setPerkChoiceList(Collections.emptyList());

                    // + ACK 할 수 있도록 셋팅
                    player.setAckState(ACK_TYPE.NO_ACK);
                }

                // "클라이언트는 perk, item 수령 애니메이션을 출력하고 ack를 보내면 됨"
                countdownMatchData.setState(nextState);

                String playersFormatted = players.stream().map(player -> player.getUsername()).collect(Collectors.joining(", "));
                System.out.printf("\t> Match `%s` (vs %s): Players: [%s]\n", countdownMatchData.getId(), matchId, playersFormatted);

                playerDataJpaRepository.saveAll(players);
                MatchData updMatchData = matchDataJPARepository.save(countdownMatchData);
                frdbRepository.setMatch(updMatchData.getId(), updMatchData);
            }, 10);

            MatchData updMatchData = matchDataJPARepository.save(matchData);
            frdbRepository.setMatch(updMatchData.getId(), updMatchData);

            System.out.printf("Countdown BEGIN for match `%s`\n", matchId);
        }
        */
    }
}
