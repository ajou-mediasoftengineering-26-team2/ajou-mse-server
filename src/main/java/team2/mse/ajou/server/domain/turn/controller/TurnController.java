package team2.mse.ajou.server.domain.turn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.turn.model.PutChoiceRequest;
import team2.mse.ajou.server.domain.turn.service.TurnService;

/**
 * @author Junseo Hwang
 */

@RestController
@RequestMapping("/turn")
public class TurnController
{
    @Autowired
    private TurnService turnService;

    @PutMapping("/choice")
    public void putHandChoice(@RequestBody PutChoiceRequest req){
        System.out.println("id = " + req.id());
        System.out.println("choice = " + req.choice());

        turnService.updateDB(req.id(), req.choice());

        try {
            turnService.updateFireBase(req.id(), req.choice());
            return;
        } catch (Exception e){
            throw new ApiError(5000, "firebase error");
        }
    }
}
