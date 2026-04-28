package team2.mse.ajou.server.apiresponse;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import team2.mse.ajou.server.apiresponse.model.ApiError;
import team2.mse.ajou.server.apiresponse.model.ApiResponse;
import team2.mse.ajou.server.apiresponse.model.ErrorData;

/**
 * 서버 비즈니스 로직 실행시 발생하는 API 에러를 이쁘게 `ApiResponse<>` 클래스로 감싸줍니다.
 *
 * @author yubin
 */
@RestControllerAdvice
public class ApiResponseExceptionHandlerAdvice {
    @ExceptionHandler(ApiError.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleUserApiError(ApiError err) {
        ApiResponse<ErrorData> body = ApiResponse.error(err.getErrorData());
        return ResponseEntity
                .status(err.getHttpStatusOverride())
                .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleInputError(HttpMessageNotReadableException err) {
        ApiResponse<ErrorData> body = ApiResponse.error(ApiError.INVALID_PARAMETER.getErrorData());
        return ResponseEntity
                .status(ApiError.INVALID_PARAMETER.getHttpStatusOverride())
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleUnknownException(Exception err) {
        // TODO: Logger 사용
        err.printStackTrace();

        ErrorData data = new ErrorData(0, "알 수 없는 서버 에러: (%s)".formatted(err.toString()));
        ApiResponse<ErrorData> body = ApiResponse.error(data);
        ResponseEntity<ApiResponse<ErrorData>> res;

        if ((err instanceof NoResourceFoundException) || (err instanceof HttpRequestMethodNotSupportedException)) {
            res = ResponseEntity
                    .notFound()
                    .build();
        } else {
            res = ResponseEntity.internalServerError().body(body);
        }

        return res;
    }
}
