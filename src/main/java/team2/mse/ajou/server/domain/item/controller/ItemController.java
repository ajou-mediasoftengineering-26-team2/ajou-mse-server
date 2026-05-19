package team2.mse.ajou.server.domain.item.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;

/**
 * Item Controller
 * Base URL: `<SERVER URL>/turn`
 *
 * @author Junseo Hwang 202322128
 */

@RestController
@RequestMapping("/item")
public class ItemController {

    @PutMapping("/ack")
    public void ackItemAnimationEnd(@RequestBody PutAckRequest req){

    }
}
