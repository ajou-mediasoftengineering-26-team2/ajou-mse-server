package team2.mse.ajou.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.server.ExportException;


@RestController
public class TurnController
{
    @Autowired
    private TurnService turnService;

    @PutMapping("/choice")
    public ResponseEntity<String> putHandChoice(
            @RequestParam String id,
            @RequestParam String choice
    ){
        try {
            turnService.putResult(id, choice);
            return ResponseEntity.ok("성공");
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("실패");
        }
    }
}
