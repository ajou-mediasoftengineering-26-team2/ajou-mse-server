package team2.mse.ajou.server.domain.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.ack.service.AckService;
import team2.mse.ajou.server.domain.firebase.service.FrdbRepository;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataJpaRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchServiceLegacy;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

import java.util.*;

/**
 * @author Junseo Hwang 202322128
 */
@Service
public class ItemService implements IItemService {
    private final PlayerDataJpaRepository playerDataJpaRepository;
    private final MatchDataJpaRepository matchDataJPARepository;
    private final FrdbRepository frdbRepository;
    private final AckService ackService;
    private final MatchServiceLegacy matchService;

    @Autowired
    public ItemService(PlayerDataJpaRepository playerDataJpaRepository,
                       MatchDataJpaRepository matchDataJPARepository,
                       MatchTurnCalcService matchTurnCalcService,
                       MatchServiceLegacy matchService,
                       FrdbRepository frdbRepository, AckService ackService) {
        this.playerDataJpaRepository = playerDataJpaRepository;
        this.matchDataJPARepository = matchDataJPARepository;
        this.frdbRepository = frdbRepository;
        this.ackService = ackService;
        this.matchService = matchService;
    }

    /**
     * 주어진 MatchData의 플레이어들에 item을 랜덤으로 지급함
     *
     * @param matchData - 아이템을 지급한 상태로 수정시킬 MatchData
     */
    public void giveRandomItem(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();

        for (PlayerData player : players) {
            List<ITEM_CODE> unownedItemList = getUnownedItem(player);

            int receivingCount;
            if (matchData.getStation().equals("SEONGSU")) {
                receivingCount = Math.min(2, unownedItemList.size());
            } else {
                receivingCount = Math.min(1, unownedItemList.size());
            }

            // 플레이어가 받을 아이템
            List<ITEM_CODE> receivingItemList = unownedItemList.subList(0, receivingCount);
            for (ITEM_CODE item : receivingItemList) {
                player.getItemList().add(item);
            }
            player.setReceivedItemList(receivingItemList);

            matchData.updatePlayer(player);
            // playerDataRepository.save(player);
        }

        // matchData.setState(MATCH_STATE.GAME_PERK_ITEM_RECEIVING);
        // matchDataRepository.save(matchData);
        // frdbService.setMatch(matchId, matchData);
    }

    /**
     * item animation end ack를 받을 때 호출 됨
     *
     * @param playerId - ack를 보낸 player id
     */
    @Override
    public void receiveItemAnimationEndAck(UUID playerId) {
        PlayerData playerData = playerDataJpaRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Not Found: " + playerId));
        MatchData matchData = matchDataJPARepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        if (!matchData.getState().isReceivingItems()) {
            throw new IllegalStateException("Item/perk/elemental receive animation ACK can be submitted only after turn result is calculated!");
        }

        if (playerData.getAckState() != ACK_TYPE.NO_ACK) {
            throw new IllegalStateException("Player is already acknowledged!");
        }

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        // 둘 다 ACK 받으면 다음 라운드로 이동
        if (isAllItemAnimationEnd(matchData)) {
            // (라운드 리셋 + state 설정)
            matchService.initializeMatchRound(matchData);
        }

        playerDataJpaRepository.saveAll(matchData.getPlayers());
        MatchData updMatchData = matchDataJPARepository.save(matchData);
        frdbRepository.setMatch(updMatchData.getId(), updMatchData);
    }

    private boolean isAllItemAnimationEnd(MatchData matchData) {
        return ackService.isAllAckReceived(matchData, ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
    }

    private List<ITEM_CODE> getUnownedItem(PlayerData playerData) {
        List<ITEM_CODE> itemList = playerData.getItemList();
        Set<ITEM_CODE> existingItems = new HashSet<>(itemList);

        // 아직 없는 아이템 코드들
        List<ITEM_CODE> unownedItems = new ArrayList<>();
        // NONE이 아이템으로 지급되면 정말로 이상하겠지요. 필터링해줍니다.
        List<ITEM_CODE> availableItems = Arrays.stream(ITEM_CODE.values())
                .filter(itemCode -> itemCode != ITEM_CODE.NONE)
                .toList();

        for (ITEM_CODE code : availableItems) {
            if (!existingItems.contains(code)) {
                unownedItems.add(code);
            }
        }

        Collections.shuffle(unownedItems);

        return unownedItems;
//        // 없는 아이템 중 랜덤 선택
//        int randomIndex = ThreadLocalRandom.current().nextInt(unownedItems.size());
//        ITEM_CODE randomCode = unownedItems.get(randomIndex);
//
//        return randomCode;
    }
}
