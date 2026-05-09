package team2.mse.ajou.server.test.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team2.mse.ajou.server.domain.shared.match.model.PlayerData;
import team2.mse.ajou.server.domain.shared.match.repository.PlayerDataRepository;
import team2.mse.ajou.server.test.model.TestResponse;


import java.util.UUID;

/** 테스트를 위한 임시 Controller
 *
 * @author Junseo Hwang
 */

@Deprecated(forRemoval = true)
@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class DBTestController
{
    private final PlayerDataRepository playerDataRepository;

    @GetMapping("/player/{id}")
    public TestResponse getChoiceByID(@PathVariable String id){
        UUID uuid = UUID.fromString(id);
        PlayerData player = playerDataRepository.findById(uuid)
                .orElseThrow(()-> new IllegalArgumentException("Not Found: " +id));

        return new TestResponse(
                player.getChoice().toString()
        );
    }
}