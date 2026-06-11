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
 * Controller related to elemental.
 * Base URL: `<SERVER URL>/elemental`
 *
 * @author Junseo Hwang 202322128
 */
@RestController
@RequestMapping("/elemental")
public class ElementalController {
    IElementalService elementalService;

    public ElementalController(IElementalService elementalService) {
        this.elementalService = elementalService;
    }

    /**
     * 플레이어의 elemental 선택을 받음
     * This receives elemental that player has choice.
     * end point: <SERVER URL>/elemental/choice
     * @param req request body
     */
    @PutMapping("/choice")
    public void putElementalChoice(@RequestBody PutElementalChoiceRequest req){
        // TODO:  DB, Firebase에 저장
        UUID id = UUID.fromString(req.playerId());
        HAND_ELEMENTAL handElemental = HAND_ELEMENTAL.valueOf(req.handElemental());

        // TODO: 현재 Test 구현은 둘에게 choice를 받으면 elemental_receiving으로 넘어감. 타이머 종료로 넘어가야함.
        elementalService.putElementalChoice(id, handElemental);
    }

    /**
     * 플레이어의 elemental 업그레이드 요청을 받음
     * This receives elemental upgrade request
     * end point: <SERVER URL>/elemental/upgrade
     * @param req request body
     */
    @PutMapping("/upgrade")
    public void putElementalUpgrade(@RequestBody PutElementalChoiceRequest req){
        UUID id = UUID.fromString(req.playerId());
        HAND_ELEMENTAL handElemental = HAND_ELEMENTAL.valueOf(req.handElemental());

        elementalService.upgradeElemental(id, handElemental);
    }

    /**
     * 플레이어의 perk 선택을 받음
     * This receives elemental animation ack
     * ack means client finish elemental receiving animation
     * end point: <SERVER URL>/elemental/ack
     * @param req request body
     */
    @PutMapping("/ack")
    public void elementalAnimationEndAck(@RequestBody PutAckRequest req){
        UUID playerId = UUID.fromString(req.playerId());

        elementalService.receiveElementalAnimationEndAck(playerId);
    }

}
