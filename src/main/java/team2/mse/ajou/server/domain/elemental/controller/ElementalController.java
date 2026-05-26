package team2.mse.ajou.server.domain.elemental.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.domain.elemental.model.PutElementalChoiceRequest;
import team2.mse.ajou.server.domain.elemental.service.IElementalService;
import team2.mse.ajou.server.domain.perk.service.IPerkService;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;
import team2.mse.ajou.server.domain.shared.match.HAND_ELEMENTAL;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.UUID;

/**
 * Base URL: `<SERVER URL>/elemental`
 *
 * @author Junseo Hwang 202322128
 */

@RestController
@RequestMapping("/elemental")
public class ElementalController {
    IElementalService elementalService;

    public ElementalController(@Qualifier("TestElementalService")IElementalService elementalService) {
        this.elementalService = elementalService;
    }

    @PutMapping("/choice")
    public void putPerkChoice(@RequestBody PutElementalChoiceRequest req){
        // TODO:  DB, Firebase에 저장
        UUID id = UUID.fromString(req.playerId());
        HAND_ELEMENTAL handElemental = HAND_ELEMENTAL.valueOf(req.handElemental());

        // TODO: 현재 Test 구현은 둘에게 choice를 받으면 elemental_receiving으로 넘어감. 타이머 종료로 넘어가야함.
        elementalService.putElementalChoice(id, handElemental);
    }

    @PutMapping("/ack")
    public void elementalAnimationEndAck(@RequestBody PutAckRequest req){
        UUID playerId = UUID.fromString(req.playerId());

        elementalService.receiveElementalAnimationEndAck(playerId);
    }

}
