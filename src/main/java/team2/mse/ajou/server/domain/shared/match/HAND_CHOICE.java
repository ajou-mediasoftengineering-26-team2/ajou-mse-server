package team2.mse.ajou.server.domain.shared.match;

public enum HAND_CHOICE {
    /** 왼손 뒤집기 */
    SINGLE_HAND_FLIP_LEFT,
    /** 오른손 뒤집기 */
    SINGLE_HAND_FLIP_RIGHT,
    /** 양손 뒤집기 */
    BOTH_HANDS_FLIP,
    /** 찌르기 */
    INSERT_BETWEEN_HANDS,
    /** 가만히 */
    SHAKE_OVER_HANDS;
}
