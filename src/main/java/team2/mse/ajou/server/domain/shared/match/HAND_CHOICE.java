package team2.mse.ajou.server.domain.shared.match;

/**
 * Hand behavior Choice
 *
 * @author Junseo Hwang 202322128
 */
public enum HAND_CHOICE {
    /**Flip Left hand
     * 왼손 뒤집기 */
    SINGLE_HAND_FLIP_LEFT,
    /**Flip right hand
     * 오른손 뒤집기 */
    SINGLE_HAND_FLIP_RIGHT,
    /**Flip both hand
     * 양손 뒤집기 */
    BOTH_HANDS_FLIP,
    /**Insert
     * 찌르기 */
    INSERT_BETWEEN_HANDS,
    /**Shake
     * 가만히 */
    SHAKE_OVER_HANDS,
    /**
     * forbidden behavior
     * 시청역에서 금지된 행동
     */
    FORBIDDEN_BEHAVIOR
}
