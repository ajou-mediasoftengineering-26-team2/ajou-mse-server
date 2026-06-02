package team2.mse.ajou.server.domain.turn.service;

import org.springframework.stereotype.Service;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;
import team2.mse.ajou.server.domain.turn.model.Damage;
import team2.mse.ajou.server.domain.turn.model.DefendEffect;

import java.util.ArrayList;
import java.util.List;

@Service
public class DamageCalcSerivce implements IDamageCalcService {
    @Override
    public List<Damage> calcDamageList(PlayerData attacker, PlayerData defender) {
        List<Damage> damageList = new ArrayList<>();

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
            int damage = 1;
            int coin = 1;



//            damageList.add(new Damage(1, ));
        }

        return damageList;
    }

    @Override
    public List<DefendEffect> calcDefendList(PlayerData defender) {
        return List.of();
    }
}
