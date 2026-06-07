package team2.mse.ajou.server.domain.shared.match.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefendData {
    @Id
    @GeneratedValue
    private Long id;

    int recoveredHp = 0;
    int coin = 5;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    List<PERK> usedPerks = new ArrayList<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    List<ITEM_CODE> usedItemCodes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    HAND_ELEMENTAL usedElemental;

    public void addRecoveredHp(int recoveredHp) {
        this.recoveredHp += recoveredHp;
    }

    public void addCoin(int coin) {
        this.coin += coin;
    }

    public void addUsedPerk(PERK perk) {
        usedPerks.add(perk);
    }

    public void addUsedItem(ITEM_CODE itemCode) {
        usedItemCodes.add(itemCode);
    }

    public void initDefendData()
    {
        recoveredHp = 0;
        coin = 5;

        if(usedPerks == null) usedPerks = new ArrayList<>();
        else usedPerks.clear();

        if(usedItemCodes == null) usedItemCodes = new ArrayList<>();
        else usedItemCodes.clear();

        usedElemental = null;
    }
}
