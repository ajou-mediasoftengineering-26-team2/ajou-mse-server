package team2.mse.ajou.server.domain.subway;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team2.mse.ajou.server.apiresponse.model.ApiError;


//202322158 이준상
//About if api response have some problem while I
@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler(ApiError.class)
    public void handleApiError(ApiError e) {
        System.out.println(e.getCode() + " " + e.getMessage());
    }
}