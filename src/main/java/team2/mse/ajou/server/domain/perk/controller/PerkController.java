package team2.mse.ajou.server.domain.perk.controller;

import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.domain.perk.model.PutPerkChoiceRequest;
import team2.mse.ajou.server.domain.perk.service.IPerkService;
import team2.mse.ajou.server.domain.shared.ack.model.PutAckRequest;
import team2.mse.ajou.server.domain.shared.match.PERK;

import java.util.UUID;

/**
 * PerkController receives Perk selected by each player.
 * Base URL: `<SERVER URL>/turn`
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

    @PutMapping("choice")
    public void putPerkChoice(@RequestBody PutPerkChoiceRequest req){
        // TODO: perk선택 DB, Firebase에 저장
        UUID id = UUID.fromString(req.playerId());
        PERK perk = PERK.valueOf(req.perk());

        perkService.putPerkChoice(id, perk);
    }

    //안 쓸지도 모르겠습니다.
    @PutMapping("ack")
    public void perkAnimationEnd(@RequestBody PutAckRequest req){
        // TODO: Ack DB, Firebase에 저장
    }
}
