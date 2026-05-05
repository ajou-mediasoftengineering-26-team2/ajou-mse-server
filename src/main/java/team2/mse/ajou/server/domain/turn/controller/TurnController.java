package team2.mse.ajou.server.domain.turn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.TestResponse;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.domain.turn.model.PutChoiceRequest;
import team2.mse.ajou.server.domain.turn.service.TurnService;

/** 매 턴마다 플레이어의 행동 선택을 입력받습니다.
 *
 * @author Junseo Hwang
 */

@RestController
@RequestMapping("/turn")
public class TurnController
{
    @Autowired
    private TurnService turnService;

    @PutMapping("/choice")
    public void putHandChoice(
            @RequestBody PutChoiceRequest req
            ){
        try {
            turnService.putPlayerInput(req.id(), req.choice());
            return;
        } catch (IllegalStateException e){
            throw new ApiError(4000, e.getMessage());
        } catch (Exception e){
            throw new ApiError(5000, "firebase error");
        }
    }
}
