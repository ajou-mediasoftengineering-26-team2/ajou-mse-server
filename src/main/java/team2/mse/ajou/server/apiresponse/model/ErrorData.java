package team2.mse.ajou.server.apiresponse.model;

/**
 * API Response에 넣어질 에러 데이터.
 *
 * @param code    에러 코드. HTTP 코드/Status와 다릅니다!
 * @param message 에러 메시지.
 * @author yubin
 */
public record ErrorData(
        int code,
        String message
) {

}
