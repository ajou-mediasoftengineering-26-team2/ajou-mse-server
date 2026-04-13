package team2.mse.ajou.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class TestController {
    @GetMapping("/test")
    public TestResponse getTestResponse() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return new TestResponse("Hello world!!", currentTime);
    }
}
