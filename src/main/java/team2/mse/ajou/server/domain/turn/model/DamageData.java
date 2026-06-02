package team2.mse.ajou.server.domain.turn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;
import team2.mse.ajou.server.domain.turn.STATUS_EFFECT;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DamageData{
    private int damage = 1;
    private int coin = 2;
    private int recoveredHp = 0;
    private ATTACK_TYPE attackType = ATTACK_TYPE.NONE;
    private List<ITEM_CODE> usedItems = new ArrayList<>();
    private List<PERK> usedPerks = new ArrayList<>();
    private List<STATUS_EFFECT> statusEffects = new ArrayList<>();
    private boolean ko = false;
    private int damageIndex = 0;

    public void addDamage(int damage) {
        this.damage += damage;
    }
    public void addCoin(int coin) {
        this.coin += coin;
    }
    public void addRecoveredHp(int recoveredHp) {
        this.recoveredHp += recoveredHp;
    }

    public void addUsedPerk(PERK perk) {
        usedPerks.add(perk);
    }

    public void addUsedItem(ITEM_CODE item) {
        usedItems.add(item);
    }

    public void addStatusEffect(STATUS_EFFECT effect) {
        statusEffects.add(effect);
    }
}
