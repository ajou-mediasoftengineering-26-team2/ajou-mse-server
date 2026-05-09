package team2.mse.ajou.server.apiresponse.model;


import jakarta.annotation.Nullable;

/**
 * Base API response structure to have a consistent formatting in API response.
 *
 * @author Ahn Yubin / 202021088
 */
public record ApiResponse<T>(
        boolean isSuccess,
        @Nullable T data,
        @Nullable ErrorData error
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<ErrorData> error(ErrorData error) {
        return new ApiResponse<>(false, null, error);
    }
}
