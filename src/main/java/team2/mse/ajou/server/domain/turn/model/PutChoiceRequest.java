package team2.mse.ajou.server.domain.turn.model;

/** 플레이어가 행동을 선택할 때 보내는 RequestBody
 *
 * @author Junseo Hwang
 */
public record PutChoiceRequest(
        String id,
        String choice
) {
}
