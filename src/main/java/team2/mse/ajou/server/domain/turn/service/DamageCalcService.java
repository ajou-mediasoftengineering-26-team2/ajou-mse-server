package team2.mse.ajou.server.domain.turn.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.item.model.IConsumableItem;
import team2.mse.ajou.server.domain.item.model.Items.ItemResistance;
import team2.mse.ajou.server.domain.item.service.ItemFactory;
import team2.mse.ajou.server.domain.perk.model.IPerk;
import team2.mse.ajou.server.domain.perk.service.PerkFactory;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.turn.model.DefendEffect;

import java.util.ArrayList;
import java.util.List;

@Service
public class DamageCalcService implements IDamageCalcService {
    @Override
    public void calcDamageList(MatchData matchData) {
        PlayerData attacker = matchData.getPlayers().get(matchData.getAttackerPlayerIdx());
        PlayerData defender = matchData.getPlayers().get(matchData.getAttackerPlayerIdx()^1);

        List<IPerk> attackerPerkList = PerkFactory.createPerkList(attacker.getPerkList());
        List<IPerk> defenderPerkList = PerkFactory.createPerkList(defender.getPerkList());

        List<IConsumableItem> attackerItemList = ItemFactory.createItemList(attacker.getItemList());
        List<IConsumableItem> defenderItemLIst = ItemFactory.createItemList(defender.getItemList());

        int attackCnt = 0;
        ATTACK_TYPE handAttackType = ATTACK_TYPE.NONE;

        // 행동에 따른 전체적인 값들 전처리
        switch (attacker.getChoice()) {
            case SINGLE_HAND_FLIP_LEFT -> {
                attackCnt = 1;
                handAttackType = ATTACK_TYPE.LEFT_HAND;
            }
            case SINGLE_HAND_FLIP_RIGHT -> {
                attackCnt = 1;
                handAttackType = ATTACK_TYPE.RIGHT_HAND;
            }
            case BOTH_HANDS_FLIP -> {
                attackCnt = 1;
                handAttackType = ATTACK_TYPE.BOTH_HAND;
            }
            case INSERT_BETWEEN_HANDS -> {
                attackCnt = 5;
                handAttackType = ATTACK_TYPE.BOTH_HAND;
            }
            case SHAKE_OVER_HANDS -> {
                attackCnt = 7;
                handAttackType = ATTACK_TYPE.BOTH_HAND;
            }
        }

        for(int i = 0; i<attackCnt; i++){
            DamageData damageData = new DamageData();
            damageData.setAttackType(handAttackType);
            damageData.setDamageIndex(i);

            matchData.addDamageData(damageData);

            // Perk 계산
            for(IPerk perk : attackerPerkList){
                perk.usePerkIfPossible(matchData);
            }
            for(IPerk perk : defenderPerkList){
                perk.usePerkIfPossible(matchData);
            }

            // Item 계산
            for(IConsumableItem item : attackerItemList){
                item.useItemIfPossible(matchData, matchData.getAttackerPlayerIdx());
            }
            for(IConsumableItem item : defenderItemLIst){
                item.useItemIfPossible(matchData, matchData.getAttackerPlayerIdx()^1);
            }

//            // 사용한 Item 제거
//            for(ITEM_CODE usedItem : attacker.getUsedItemList()) {
//                attacker.getItemList().remove(usedItem);
//            }
//            for(ITEM_CODE usedItem : defender.getUsedItemList()) {
//                defender.getItemList().remove(usedItem);
//            }

            defender.setHp(Math.max(0,defender.getHp()-damageData.getDamage()));
        }

        // 사용한  ItemResistance 제거 (얘만 턴 전체 적용이라 이렇게 됨;;)
        for(IConsumableItem item : defenderItemLIst){
            if(item instanceof ItemResistance resistance){
                if(!resistance.isUsed()) continue;
                defender.getItemList().remove(resistance.getItemCode());
            }
        }
    }

    @Override
    public List<DefendEffect> calcDefendList(MatchData matchData) {
        return List.of();
    }
}
