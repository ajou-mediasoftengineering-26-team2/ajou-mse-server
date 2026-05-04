package team2.mse.ajou.server.domain.firebase;

import java.time.format.DateTimeFormatter;

/**
 * FRDB에 사용되는 상수들. (e.g. 시간 포맷)
 *
 * @author Ahn yubin / 202021088
 */
public final class FrdbConstants {
    public static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN);
}
