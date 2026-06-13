package team2.mse.ajou.server.domain.elemental.service;

import team2.mse.ajou.server.domain.elemental.model.Elemental;
import team2.mse.ajou.server.domain.elemental.model.IElemental;
import team2.mse.ajou.server.domain.elemental.model.elementals.*;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;

/**
 * Elemental Factory
 * static method를 위해서 존재한다. 이 클래스로 객체를 만들 필요가 없다
 * @author Junseo Hwang
 */
public class ElementalFactory {
    /**
     * elemental enum에 맞는 elemental 로직 구현체를 생성한다.
     * This creates an implementation of the elemental logic that matches the elemental enum.
     * @param elemental elemental enum
     * @return implementation of the elemental logic that matches the elemental enum
     */
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
