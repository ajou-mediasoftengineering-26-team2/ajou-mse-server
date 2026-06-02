package team2.mse.ajou.server.domain.turn.model;

import team2.mse.ajou.server.domain.shared.match.HAND_CHOICE;

public record Damage(
        HAND_CHOICE atkType,
        int damage
) {
}
