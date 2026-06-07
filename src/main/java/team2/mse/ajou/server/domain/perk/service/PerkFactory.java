package team2.mse.ajou.server.domain.perk.service;

import team2.mse.ajou.server.domain.perk.model.IPerk;
import team2.mse.ajou.server.domain.perk.model.perks.*;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Junseo Hwang 202322128
 */
public class PerkFactory {

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