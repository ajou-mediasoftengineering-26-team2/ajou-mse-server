package team2.mse.ajou.server.domain.round.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.round.service.IRoundService;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;

import java.util.UUID;

/**
 *
 * Base URL: `<SERVER URL>/round`
 *
 * @author Junseo Hwang 202322128
 */
@RestController
@RequestMapping("/round")
public class RoundController {
    IRoundService roundService;

    public RoundController(IRoundService roundService) {
        this.roundService = roundService;
    }

    @PutMapping("/start-ack")
    public void roundStartAnimation(@RequestBody PutAckRequest req) {
        UUID id = UUID.fromString(req.playerId());
        roundService.receiveRoundStart(id);
    }

    @PutMapping("/end-ack")
    public void roundEndAnimation(@RequestBody PutAckRequest req) {
        UUID id = UUID.fromString(req.playerId());
        roundService.receiveRoundEnd(id);
    }
}
