package team2.mse.ajou.server.domain.turn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.TestResponse;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.turn.model.PutChoiceRequest;
import team2.mse.ajou.server.domain.turn.service.TurnService;

/**
 * Each turn receives a player's choices.
 * Base URL: `<SERVER URL>/turn`
 *
 * @author Junseo Hwang 202322128
 */

@RestController
@RequestMapping("/turn")
public class TurnController
{
    @Autowired
    private TurnService turnService;

    /**
     * Each turn receives a player's handChoice.
     * @param req Request body
     */
    @PutMapping("/choice")
    public void putHandChoice(
            @RequestBody PutChoiceRequest req
            ){
        try {
            turnService.putPlayerInput(req.id(), req.choice());
            return;
        }
        // When a request is received while it is not the player’s turn.
        catch (IllegalStateException e){
            throw new ApiError(4000, e.getMessage());
        }
        // When Firebase cannot be used.
        catch (Exception e){
            throw new ApiError(5000, "firebase error");
        }
    }
}
