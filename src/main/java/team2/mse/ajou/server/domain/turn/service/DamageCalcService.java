package team2.mse.ajou.server.domain.turn.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.elemental.model.IElemental;
import team2.mse.ajou.server.domain.elemental.service.ElementalFactory;
import team2.mse.ajou.server.domain.item.model.IConsumableItem;
import team2.mse.ajou.server.domain.item.model.Items.ItemResistance;
import team2.mse.ajou.server.domain.item.service.ItemFactory;
import team2.mse.ajou.server.domain.perk.model.IPerk;
import team2.mse.ajou.server.domain.perk.service.PerkFactory;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;
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

        int attackerIndex = matchData.getAttackerPlayerIdx();
        int defenderIndex = matchData.getAttackerPlayerIdx()^1;

        IElemental attackerElemental = ElementalFactory.createElemental(attacker.getHandElemental());
        IElemental defenderElemental = ElementalFactory.createElemental(defender.getHandElemental());

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
            if(i == 0) damageData.setCoin(2);

            matchData.addDamageData(damageData);

            // Elemental 계산
            attackerElemental.useElementalIfPossible(matchData, attackerIndex);
            defenderElemental.useElementalIfPossible(matchData, defenderIndex);

            if(damageData.getAttackType() == ATTACK_TYPE.MISS){
                continue;
            }

            // Perk 계산
            for(IPerk perk : attackerPerkList){
                perk.usePerkIfPossible(matchData, attackerIndex);
            }
            for(IPerk perk : defenderPerkList){
                perk.usePerkIfPossible(matchData, defenderIndex);
            }

            // Item 계산
            for(IConsumableItem item : attackerItemList){
                item.useItemIfPossible(matchData, attackerIndex);
            }
            for(IConsumableItem item : defenderItemLIst){
                item.useItemIfPossible(matchData, defenderIndex);
            }

            damageData.setDamage(Math.max(0, damageData.getDamage()));

            defender.setHp(Math.max(0,defender.getHp()-damageData.getDamage()));
            attacker.setHp(Math.min(attacker.getMaxHp(),attacker.getHp()+damageData.getRecoveredHp()));
            attacker.setCoin(attacker.getCoin()+damageData.getCoin());

            if (defender.getHp() <= 0) {
                damageData.setKo(true);
            }
        }

        // Burning 상태이상시 BurnDamage
        if(defender.getStatusEffectList().contains(STATUS_EFFECT.BURNING)){
            DamageData damageData = new DamageData();
            damageData.setAttackType(ATTACK_TYPE.BURNING);
            damageData.setDamageIndex(attackCnt);
            damageData.setDamage(0);

            matchData.addDamageData(damageData);

            // Elemental 계산
            attackerElemental.useElementalIfPossible(matchData, attackerIndex);
            defenderElemental.useElementalIfPossible(matchData, defenderIndex);

            // Perk 계산
            for(IPerk perk : attackerPerkList){
                perk.usePerkIfPossible(matchData, attackerIndex);
            }
            for(IPerk perk : defenderPerkList){
                perk.usePerkIfPossible(matchData, defenderIndex);
            }

            // Item 계산
            for(IConsumableItem item : attackerItemList){
                item.useItemIfPossible(matchData, attackerIndex);
            }
            for(IConsumableItem item : defenderItemLIst){
                item.useItemIfPossible(matchData, defenderIndex);
            }

            damageData.setDamage(Math.max(0, damageData.getDamage()));

            defender.setHp(Math.max(0,defender.getHp()-damageData.getDamage()));
            attacker.setCoin(attacker.getCoin()+damageData.getCoin());

            if (defender.getHp() <= 0) {
                damageData.setKo(true);
            }
        }

        removeResistance(defenderItemLIst, defender);
    }

    @Override
    public void calcDefendEffect(MatchData matchData) {
        PlayerData defender = matchData.getPlayers().get(matchData.getAttackerPlayerIdx()^1);
        int defenderIndex = matchData.getAttackerPlayerIdx()^1;

        IElemental defenderElemental = ElementalFactory.createElemental(defender.getHandElemental());
        List<IPerk> defenderPerkList = PerkFactory.createPerkList(defender.getPerkList());
        List<IConsumableItem> defenderItemLIst = ItemFactory.createItemList(defender.getItemList());

        DefendData defendData = matchData.getDefendData();
        defendData.initDefendData();

        defenderElemental.useElementalIfPossible(matchData, defenderIndex);
        for(IPerk perk : defenderPerkList){
            perk.usePerkIfPossible(matchData, defenderIndex);
        }
        for(IConsumableItem item : defenderItemLIst){
            item.useItemIfPossible(matchData, defenderIndex);
        }
        
        defender.setHp(Math.min(defender.getMaxHp(), defender.getHp()+ defendData.getRecoveredHp()));
        defender.setCoin(defender.getCoin() + defendData.getCoin());
    }

    // 사용한  ItemResistance 제거 (얘만 턴 전체 적용이라 이렇게 됨;;)
    private void removeResistance(List<IConsumableItem> defenderItemLIst, PlayerData defender) {
        for(IConsumableItem item : defenderItemLIst){
            if(item instanceof ItemResistance resistance){
                if(!resistance.isUsed()) continue;
                defender.getItemList().remove(resistance.getItemCode());
            }
        }
    }
}
