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
 * Catches any unhandled exceptions into `ApiResponse<>` class to make error handling on client side simpler.
 *
 * @author Ahn Yubin / 202021088
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

        ErrorData data = new ErrorData(0, "UNKNOWN SERVER ERROR: (%s)".formatted(err.toString()));
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
