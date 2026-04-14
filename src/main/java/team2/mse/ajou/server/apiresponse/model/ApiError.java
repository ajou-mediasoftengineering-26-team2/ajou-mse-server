package team2.mse.ajou.server.apiresponse.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 에러입니다. 서버에서 "예상 가능한" 에러시 throw 해버리면 됩니다. (EX: 클라이언트에서 이상한 값을 보냈다던가, DB에 값이 존재하지 않는다던가, Firebase 연결이 끊겼다던가...)
 *
 * @author yubin
 */
@Getter
public class ApiError extends RuntimeException {
    /**
     * 에러 코드. HTTP 코드/Status와 다릅니다! 아래 미리 생성된 상수 오브젝트들을 참고해주세요.
     */
    private final int code;
    /**
     * `ApiResponse` 에 넣어줄 `ErrorData` 클래스. 생성자에서 자동으로 생성되지 신경쓰지 마세요.
     */
    private final ErrorData errorData;
    /**
     * HTTP Status 오버라이드. 200 OK 말고, 400 Bad request 등 값을 넣어주고 싶으면 이걸 생성자에서 다른 것으로 넣어주면 됩니다.
     */
    private final HttpStatus httpStatusOverride;

    // 자주 쓰이는 종류는 미리 생성해둬서 바로 참조 가능하게...
    // 여기서의 에러 코드는 음수입니다. 왜냐면... 그래야 사용자 지정 API 에러랑 뭔가 분간이 되지 않을깝쇼
    // public static final ApiError UNKNOWN = new ApiError(0, "올바르지 못한 파라미터 값"); // 다른 곳에서 사용
    public static final ApiError INVALID_PARAMETER = new ApiError(-1, "올바르지 못한 파라미터 값");
    public static final ApiError VALUE_DOES_NOT_EXIST = new ApiError(-2, "값이 존재하지 않음");

    /**
     * 사용자 지정 API 에러를 처리할 때 사용합니다.
     * 자동으로 HTTP Status를 200 (OK)로 설정합니다.
     *
     * @param code 에러 코드 (양수 권장)
     * @param msg  에러 메시지
     */
    public ApiError(int code, String msg) {
        super(msg);

        // 내부변수 초기화
        this.code = code;
        this.errorData = new ErrorData(code, msg);
        this.httpStatusOverride = HttpStatus.OK;
    }

    /**
     * HTTP Status가 200이 아닌 사용자 지정 API 에러를 처리할 때 사용합니다.
     *
     * @param code               에러 코드 (양수 권장)
     * @param msg                에러 메시지
     * @param httpStatusOverride HTTP Status
     */
    public ApiError(int code, String msg, HttpStatus httpStatusOverride) {
        super(msg);

        // 내부변수 초기화
        this.code = code;
        this.errorData = new ErrorData(code, msg);
        this.httpStatusOverride = httpStatusOverride;
    }
}
