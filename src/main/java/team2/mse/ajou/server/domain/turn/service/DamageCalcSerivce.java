package team2.mse.ajou.server.domain.turn.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.perk.model.IPerk;
import team2.mse.ajou.server.domain.perk.service.PerkFactory;
import team2.mse.ajou.server.domain.shared.match.model.MatchData;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.turn.model.DefendEffect;

import java.util.ArrayList;
import java.util.List;

@Service
public class DamageCalcSerivce implements IDamageCalcService {
    @Override
    public List<DamageData> calcDamageList(MatchData matchData) {
        List<DamageData> damageDataList = new ArrayList<>();

        PlayerData attacker = matchData.getPlayers().get(matchData.getAttackerPlayerIdx());
        PlayerData defender = matchData.getPlayers().get(matchData.getAttackerPlayerIdx()^1);

        List<IPerk> attackerPerkList = PerkFactory.createPerkList(attacker.getPerkList());
        List<IPerk> defenderPerkList = PerkFactory.createPerkList(defender.getPerkList());

        int attackCnt = 0;
        ATTACK_TYPE handAttackType = ATTACK_TYPE.NONE;

        // 행동에 따른 전체적인 값들 전처리
        switch (attacker.getChoice()) {
        case SINGLE_HAND_FLIP_LEFT:
            attackCnt = 1;
            handAttackType = ATTACK_TYPE.LEFT_HAND;

        case SINGLE_HAND_FLIP_RIGHT:
            attackCnt = 1;
            handAttackType = ATTACK_TYPE.RIGHT_HAND;

        case BOTH_HANDS_FLIP:
            attackCnt = 1;
            handAttackType = ATTACK_TYPE.BOTH_HAND;

        case INSERT_BETWEEN_HANDS:
            attackCnt = 5;
            handAttackType = ATTACK_TYPE.BOTH_HAND;

        case SHAKE_OVER_HANDS:
            attackCnt = 7;
            handAttackType = ATTACK_TYPE.BOTH_HAND;
        }

        for(int i = 0; i<attackCnt; i++){
            damageDataList.add(new DamageData());
            damageDataList.getLast().setAttackType(handAttackType);

            for(IPerk perk : attackerPerkList){
                perk.usePerkIfPossible(matchData);
            }
            for(IPerk perk : defenderPerkList){
                perk.usePerkIfPossible(matchData);
            }
        }

        return damageDataList;
    }

    @Override
    public List<DefendEffect> calcDefendList(MatchData matchData) {
        return List.of();
    }
}
