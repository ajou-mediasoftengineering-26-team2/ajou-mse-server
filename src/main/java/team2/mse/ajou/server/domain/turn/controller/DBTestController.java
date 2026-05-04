package team2.mse.ajou.server.domain.turn.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.domain.turn.model.Player;
import team2.mse.ajou.server.domain.turn.model.PutChoiceRequest;
import team2.mse.ajou.server.domain.turn.model.TestResponse;
import team2.mse.ajou.server.domain.turn.repository.IPlayerRepository;
import team2.mse.ajou.server.domain.turn.service.TurnService;

/**
 * @author Junseo Hwang
 */

@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class DBTestController
{
    private final IPlayerRepository playerRepository;

    @GetMapping("/player/{id}")
    public TestResponse getChoiceByID(@PathVariable String id){
        Player player = playerRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: " +id));

        return new TestResponse(
                player.getChoice()
        );
    }

    @PostMapping("/player")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player savedPlayer = playerRepository.save(player);
        return ResponseEntity.ok(savedPlayer);
    }
}
