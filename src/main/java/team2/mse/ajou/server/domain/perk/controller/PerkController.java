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
 * Base URL: `<SERVER URL>/turn`
 *
 * @author Junseo Hwang 202322128
 */

@RestController
@RequestMapping("/perk")
public class PerkController {
    IPerkService perkService;

    public PerkController(@Qualifier("TestPerkService")IPerkService perkService) {
        this.perkService = perkService;
    }

    @PutMapping("choice")
    public void putPerkChoice(@RequestBody PutPerkChoiceRequest req){
        // TODO: perk선택 DB, Firebase에 저장
        UUID id = UUID.fromString(req.playerId());
        PERK perk = PERK.valueOf(req.perk());

        // TODO: 현재 Test 구현은 둘에게 perk choice를 받으면 perk_item_receiving으로 넘어감. 타이머 종료로 넘어가야함.
        perkService.putPerkChoice(id, perk);
    }

    //안 쓸지도 모르겠습니다.
    // 아마 안쓸것같습니다.
    @PutMapping("ack")
    public void perkAnimationEnd(@RequestBody PutAckRequest req){
        // TODO: Ack DB, Firebase에 저장
    }
}
