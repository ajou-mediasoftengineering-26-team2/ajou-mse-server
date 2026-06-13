package team2.mse.ajou.server.domain.perk.service;

import team2.mse.ajou.server.domain.perk.model.IPerk;
import team2.mse.ajou.server.domain.perk.model.perks.*;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.ArrayList;
import java.util.List;

/**
 * Perk Factory
 * @author Junseo Hwang 202322128
 */
public class PerkFactory {

    /**
     * perk enum에 맞는 perk 로직 구현체를 생성한다.
     * This creates an implementation of the perk logic that matches the perk enum.
     * @param perk perk enum
     * @return implementation of the perk logic that matches the perk enum
     */
    public static IPerk createPerk(PERK perk) {
        return switch (perk) {
            case IRON_FIST -> new PerkIronFist();
            case VAMPIRISM -> new PerkVampirism();
            case THRIFTY -> new PerkThrifty();
            case GRIT -> new PerkGrit();
            case TAUNT -> new PerkTaunt();
            case FOCUS -> new PerkFocus();
            case LUCK -> new PerkLuck();
            case UNYIELDING -> new PerkUnyielding();
            case INSERT_MASTER -> new PerkInsertMaster();
            case FLEXIBLE_HANDS -> new PerkFlexibleHands();
            case FLEXIBLE_MIND -> new PerkFlexibleMind();
            case RICH -> new PerkRich();
            case WISE_INVESTOR -> new PerkWiseInvestor();
        };
    }

    /**
     * perk enum list를 perk 로직 구현체 list로 변환한다.
     * Converts a perk enum list into a list of perk logic implementations.
     * @param perkEnumList perk enum list
     * @return list of implementation of the perk logic that matches the list of perk enum
     */
    public static List<IPerk> createPerkList(List<PERK> perkEnumList) {
        List<IPerk> perkList = new ArrayList<>();

        if (perkEnumList == null) {
            return perkList;
        }

        for (PERK perk : perkEnumList) {
            perkList.add(createPerk(perk));
        }

        return perkList;
    }
}