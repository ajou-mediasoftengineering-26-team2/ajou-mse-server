package team2.mse.ajou.server.domain.ack.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.mse.ajou.server.domain.ack.service.AckTestService;
import team2.mse.ajou.server.domain.shared.ack.ACK_TYPE;

import java.util.UUID;

/**
 * User test ack API endpoints.
 * Base URL: `<SERVER URL>/test/ack`
 *
 * @author Ahn Yubin / 202021088
 */
@RestController
@RequestMapping("/test")
public class AckTestController {
    private final AckTestService ackTestService;

    public AckTestController(AckTestService ackTestService) {
        this.ackTestService = ackTestService;
    }

    /**
     * Test ACK
     *
     * @param req Request body
     * @return Response body
     */
    @PutMapping("/ack")
    public void getPlayer(
            @RequestBody PutTestAckReq req
    ) {
        ackTestService.putAck(req.playerId, ACK_TYPE.valueOf(req.ack));
    }

    public record PutTestAckReq(
            UUID playerId,
            String ack
    ) {

    }
}
