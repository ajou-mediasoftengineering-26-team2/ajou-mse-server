package team2.mse.ajou.server.domain.perk.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.domain.perk.model.PutPerkChoiceRequest;
import team2.mse.ajou.server.domain.perk.service.IPerkService;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.UUID;

/**
 * PerkController receives Perk selected by each player.
 * Base URL: `<SERVER URL>/perk`
 *
 * @author Junseo Hwang 202322128
 */

@RestController
@RequestMapping("/perk")
public class PerkController {
    IPerkService perkService;

    public PerkController(IPerkService perkService) {
        this.perkService = perkService;
    }

    /**
     * 플레이어의 perk 선택을 받음
     * This receives perk that player has choice.
     * end point: <SERVER URL>/perk/choice
     * @param req request body
     */
    @PutMapping("choice")
    public void putPerkChoice(@RequestBody PutPerkChoiceRequest req){
        UUID id = UUID.fromString(req.playerId());
        PERK perk = PERK.valueOf(req.perk());

        perkService.putPerkChoice(id, perk);
    }

    /**
     * Not used (due to a change in the design)
     * @param req request body
     */
    @PutMapping("ack")
    public void perkAnimationEnd(@RequestBody PutAckRequest req){
        UUID id = UUID.fromString(req.playerId());

        perkService.putAck(id);
    }
}
