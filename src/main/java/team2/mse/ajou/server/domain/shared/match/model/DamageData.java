package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.STATUS_EFFECT;
import team2.mse.ajou.server.domain.turn.ATTACK_TYPE;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junseo Hwang 202322128
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DamageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int damage = 1;
    private int coin = 0;
    private int recoveredHp = 0;
    private ATTACK_TYPE attackType = ATTACK_TYPE.NONE;

    private HAND_ELEMENTAL handElemental = HAND_ELEMENTAL.NONE;

    /**
     * Used Item
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "damage_data_used_items",
            joinColumns = @JoinColumn(name = "damage_data_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "item_code")
    private List<ITEM_CODE> usedItems = new ArrayList<>();

    /**
     * Used Perk
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "damage_data_used_perks",
            joinColumns = @JoinColumn(name = "damage_data_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "perk")
    private List<PERK> usedPerks = new ArrayList<>();

    /**
     * status Effects
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "damage_data_status_effects",
            joinColumns = @JoinColumn(name = "damage_data_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "status_effect")
    private List<STATUS_EFFECT> statusEffects = new ArrayList<>();



    private boolean ko = false;
    private int damageIndex = 0;

    public DamageData(DamageData damageData) {
        this.id = damageData.id;
        this.damage = damageData.damage;
        this.coin = damageData.coin;
        this.recoveredHp = damageData.recoveredHp;
        this.attackType = damageData.attackType;
        this.usedItems = new ArrayList<>(damageData.usedItems);
        this.usedPerks = new ArrayList<>(damageData.usedPerks);
        this.statusEffects = new ArrayList<>(damageData.statusEffects);
        this.ko = damageData.ko;
        this.damageIndex = damageData.damageIndex;
    }

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
