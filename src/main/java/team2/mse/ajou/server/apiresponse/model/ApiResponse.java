package team2.mse.ajou.server.apiresponse.model;


import jakarta.annotation.Nullable;

/**
 * 공통으로 상속받을 API 응답입니다.
 *
 * @author yubin
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
