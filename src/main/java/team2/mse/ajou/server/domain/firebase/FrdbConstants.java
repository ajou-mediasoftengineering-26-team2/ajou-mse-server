package team2.mse.ajou.server.domain.firebase;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * FRDB (Firebase Realtime DB) related constants. (e.g. Timestamp pattern/formats)
 *
 * @author Ahn Yubin / 202021088
 */
public final class FrdbConstants {
    public static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public final static ZoneId TIME_ZONE_ID = ZoneId.of("Asia/Seoul");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN).withZone(TIME_ZONE_ID);
}
