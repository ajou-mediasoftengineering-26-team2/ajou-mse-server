package team2.mse.ajou.server.apiresponse.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API Error. Throw this when "expected" error happens (e.g. user input error, data not existing in DB, lost Firebase connection)
 *
 * @author Ahn Yubin / 202021088
 */
@Getter
public class ApiError extends RuntimeException {
    /**
     * Error code. Different from HTTP Status! See below `static final` objects for reference.
     */
    private final int code;
    /**
     * `ErrorData` to put inside the `ApiResponse`. Automatically initialized in constructor.
     */
    private final ErrorData errorData;
    /**
     * HTTP Status override. If you want to send anything other than `200 (OK)` (e.g. `400 (Bad request)`), set to other values in constructor.
     */
    private final HttpStatus httpStatusOverride;

    // Predefined, commonly-used error objects that can be re-used.
    // Negative error code is used to differentiate them from manually created `ApiError`s.
    public static final ApiError INVALID_PARAMETER = new ApiError(-1, "Invalid parameter", HttpStatus.BAD_REQUEST);
    public static final ApiError VALUE_DOES_NOT_EXIST = new ApiError(-2, "Value does not exist");

    /**
     * Automatically sets HTTP Status to `200 (OK)`.
     *
     * @param code Error code. Positive numbers are recommended.
     * @param msg  Error message.
     */
    public ApiError(int code, String msg) {
        super(msg);

        this.code = code;
        this.errorData = new ErrorData(code, msg);
        this.httpStatusOverride = HttpStatus.OK;
    }

    /**
     * For non `200 (OK)` HTTP Status.
     *
     * @param code               Error code. Positive numbers are recommended.
     * @param msg                Error message.
     * @param httpStatusOverride HTTP Status.
     */
    public ApiError(int code, String msg, HttpStatus httpStatusOverride) {
        super(msg);

        this.code = code;
        this.errorData = new ErrorData(code, msg);
        this.httpStatusOverride = httpStatusOverride;
    }
}
