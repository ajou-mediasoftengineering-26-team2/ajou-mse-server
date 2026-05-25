package team2.mse.ajou.server.domain.item.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.item.service.IItemService;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;

import java.util.UUID;

/**
 *
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

    @PutMapping("/ack")
    public void itemReceiveAnimationEnd(@RequestBody PutAckRequest req){
        UUID id = UUID.fromString(req.playerId());

        itemService.receiveItemAnimationEndAck(id);
    }
}
