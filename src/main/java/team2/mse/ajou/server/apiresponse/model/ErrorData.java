package team2.mse.ajou.server.apiresponse.model;

/**
 * Error data. Part of `ApiResponse<>`.
 *
 * @param code    Error code. Different from HTTP Status!
 * @param message Error message.
 * @author Ahn Yubin / 202021088
 */
public record ErrorData(
        int code,
        String message
) {

}
