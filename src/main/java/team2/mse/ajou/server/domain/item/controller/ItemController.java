package team2.mse.ajou.server.domain.item.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.item.service.IItemService;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;

import java.util.UUID;

/**
 * Base URL: `<SERVER URL>/item`
 *
 * @author Junseo Hwang 202322128
 */
@RestController
@RequestMapping("/item")
public class ItemController {
    IItemService itemService;

    public ItemController(IItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * perk을 적용받고 item을 받는 애니메이션 출력이 완료됨을 알리는 ack를 받음
     * This receives PERK_ITEM_RECEIVING_ANIMATION_END ACK
     * end point: <SERVER URL>/item/ack
     * @param req request body
     */
    @PutMapping("/ack")
    public void itemReceiveAnimationEnd(@RequestBody PutAckRequest req){
        UUID id = UUID.fromString(req.playerId());

        itemService.receiveItemAnimationEndAck(id);
    }
}
