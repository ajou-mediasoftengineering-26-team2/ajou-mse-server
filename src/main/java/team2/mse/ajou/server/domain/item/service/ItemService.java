package team2.mse.ajou.server.domain.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.firebase.service.FrdbService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.MATCH_STATE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.MatchDataRepository;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.shared.match.service.MatchService;
import team2.mse.ajou.server.domain.shared.match.service.MatchTurnCalcService;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Junseo Hwang 202322128
 */
@Service
public class ItemService implements IItemService {
    private final PlayerDataRepository playerDataRepository;
    private final MatchDataRepository matchDataRepository;
    private final FrdbService frdbService;

    @Autowired
    public ItemService(PlayerDataRepository playerDataRepository,
                           MatchDataRepository matchDataRepository,
                           MatchTurnCalcService matchTurnCalcService,
                           MatchService matchService,
                           FrdbService frdbService) {
        this.playerDataRepository = playerDataRepository;
        this.matchDataRepository = matchDataRepository;
        this.frdbService = frdbService;
    }

    /**
     * item을 랜덤으로 지급함.
     *
     * @param matchId - 아이템을 지급할 match id
     */
    public void giveRandomItem(UUID matchId) {
        MatchData matchData = matchDataRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + matchId));

        List<PlayerData> players = matchData.getPlayers();

        for(PlayerData player: players) {
            List<ITEM_CODE> unownedItemList = getUnownedItem(player);

            int receivingCount;
            if(matchData.getStation().equals("SEONGSU")){
                receivingCount = Math.min(2, unownedItemList.size());
            }
            else{
                receivingCount = Math.min(1, unownedItemList.size());
            }

            // 플레이어가 받을 아이템
            List<ITEM_CODE> receivingItemList = unownedItemList.subList(0, receivingCount);
            for(ITEM_CODE item: receivingItemList){
                player.getItemList().add(item);
            }
            player.setReceivedItemList(receivingItemList);

            matchData.updatePlayer(player);
            playerDataRepository.save(player);
        }
        matchData.setState(MATCH_STATE.GAME_PERK_ITEM_RECEIVING);
        matchDataRepository.save(matchData);

        frdbService.setMatch(matchId, matchData);
    }

    /**
     * item animation end ack를 받을 때 호출 됨
     * @param playerId - ack를 보낸 player id
     */
    @Override
    public void receiveItemAnimationEndAck(UUID playerId) {
        PlayerData playerData = playerDataRepository.findById(playerId)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: "+playerId));

        MatchData matchData = matchDataRepository.findById(playerData.getJoinedMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Match Not Found: " + playerData.getJoinedMatchId()));

        playerData.setAckState(ACK_TYPE.ITEM_RECEIVE_ANIMATION_END);
        matchData.updatePlayer(playerData);

        playerDataRepository.save(playerData);
        matchDataRepository.save(matchData);

        // 둘다 ack 받으면 round 시작
        if(isAllItemAnimationEnd(matchData)){
            matchData.setState(MATCH_STATE.GAME_ROUND_START_ANIMATION);
            MatchData updMatchData = matchDataRepository.save(matchData);
            frdbService.setMatch(updMatchData.getId(), updMatchData);
        }
    }

    private boolean isAllItemAnimationEnd(MatchData matchData) {
        List<PlayerData> players = matchData.getPlayers();
        if(players.size()!=2) return false;
        for(PlayerData player : players) {
            if(player.getAckState() != ACK_TYPE.TURN_ANIMATION_END) return false;
        }
        return true;
    }

    private List<ITEM_CODE> getUnownedItem(PlayerData playerData) {
        List<ITEM_CODE> itemList = playerData.getItemList();
        Set<ITEM_CODE> existingItems = new HashSet<>(itemList);

        // 아직 없는 아이템 코드들
        List<ITEM_CODE> unownedItems = new ArrayList<>();

        for (ITEM_CODE code : ITEM_CODE.values()) {
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
