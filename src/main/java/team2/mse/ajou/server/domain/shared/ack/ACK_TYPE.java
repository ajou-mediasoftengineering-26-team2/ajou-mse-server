package team2.mse.ajou.server.domain.shared.ack;

/**
 * Ack Type
 * 각 클라이언트가 애니메이션이나 선택이 완료 되었음을 알리는 신호입니다.
 * 서버는 두 클라이언트 모두에게 Ack를 받으면 흐름에 맞는 다음 로직을 실행하면 됩니다.
 *
 * @author Junseo Hwang 202322128
 */
public enum ACK_TYPE {
    /**
     * Ack를 수신하기 전 상태
     * 서버가 클라이언트의 Ack를 받기 전에는 플레이어 ack 상태가 NO_ACK입니다.
     * NO_ACK는 클라이언트가 보내는 Ack가 아닙니다.
     */
    NO_ACK,

    /**
     * 클라이언트가 match시작 컷신이 끝나면 보내는 Ack
     * CUT_SCENE_END를 받았다면 turn로직을 작동해야 합니다.
     */
    CUT_SCENE_END,

    /**
     * 클라이언트가 공격, 방어 여부에 따른 애니메이션이 끝나면 보내는 Ack
     * Item 사용 애니메이션도 포함됩니다.
     * TURN_ANIMATION_END를 받았다면 turn로직을 작동해야 합니다. (라운드를 끝내는 것 포합입니다.)
     */
    TURN_ANIMATION_END,

    /**
     * 클라이언트가 Round 종료 애니메이션이 끝나면 보내는 Ack
     * ROUND_END Ack를 받았다면 상점 로직을 작동해야 합니다.
     */
    ROUND_END,

    /**
     * 클라이언트가 상점 선택이 끝나면 받는 Ack
     * 필요 없어질 지도 모르겠습니다..
     */
    SHOP_END,

    /**
     * 클라이언트가 랜덤 아이템을 받는 애니메이션이 끝나면 보내는 Ack
     * ITEM_RECEIVE_ANIMATION을 받았다면 Round 로직을 작동해야 합니다.
     */
    ITEM_RECEIVE_ANIMATION,

    /**
     * 매치가 끝난 후 클라이언트가 모든 작업을 완료하고 보내는 Ack
     * MATCH_END를 받았다면 match를 모두 정리해야 합니다.
     */
    MATCH_END,

}
