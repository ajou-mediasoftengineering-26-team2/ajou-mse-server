package team2.mse.ajou.server.domain.firebase.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.ITEM_CODE;
import team2.mse.ajou.server.domain.shared.match.PERK;
import team2.mse.ajou.server.domain.shared.match.model.DamageData;
import team2.mse.ajou.server.domain.shared.match.model.DefendData;

import java.util.ArrayList;
import java.util.List;

@Data
public class FrdbDefendData {
    int recoveredHp;
    int coin;

    List<PERK> usedPerks = new ArrayList<>();
    List<ITEM_CODE> usedItemCodes = new ArrayList<>();
    HAND_ELEMENTAL usedElemental;

    public static FrdbDefendData from(DefendData defendData){
        FrdbDefendData data = new FrdbDefendData();

        if(defendData != null){
            data.setRecoveredHp(defendData.getRecoveredHp());
            data.setCoin(defendData.getCoin());
            data.setUsedPerks(defendData.getUsedPerks());
            data.setUsedItemCodes(defendData.getUsedItemCodes());
            data.setUsedElemental(defendData.getUsedElemental());
        }

        return data;
    }
}
