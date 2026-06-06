package team2.mse.ajou.server.domain.elemental.service;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.elemental.model.IElemental;
import team2.mse.ajou.server.domain.elemental.model.elementals.*;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;

public class ElementalFactory {
    public static IElemental createElemental(HAND_ELEMENTAL elemental) {
        if(elemental == null) {
            return null;
        }
        return switch (elemental) {
            case FIRE -> new ElementalFire();
            case LIGHTNING -> new ElementalLightning();
            case PLANT ->  new ElementalPlant();
            case POISON ->  new ElementalPoison();
            case WATER ->  new ElementalWater();
            case WIND ->  new ElementalWind();
            case NONE -> new ElementalNone();
            default -> null;
        };
    }
}
