package team2.mse.ajou.server.domain.firebase.model;

import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;

import java.util.List;

@Data
public class FrdbDamageData {
    private int damage;
    private int coin;
    private int recoveredHp;
    private ATTACK_TYPE attackType;
    private List<ITEM_CODE> usedItems;
    private List<PERK> usedPerks;
    private List<STATUS_EFFECT> statusEffects;
    private boolean ko;
    private int damageIndex;

    public static FrdbDamageData from(DamageData damageData) {
        FrdbDamageData data = new FrdbDamageData();

        data.setDamage(damageData.getDamage());
        data.setCoin(damageData.getCoin());
        data.setRecoveredHp(damageData.getRecoveredHp());
        data.setAttackType(damageData.getAttackType());
        data.setUsedItems(damageData.getUsedItems());
        data.setUsedPerks(damageData.getUsedPerks());
        data.setStatusEffects(damageData.getStatusEffects());
        data.setKo(damageData.isKo());
        data.setDamageIndex(damageData.getDamageIndex());

        return data;
    }
}