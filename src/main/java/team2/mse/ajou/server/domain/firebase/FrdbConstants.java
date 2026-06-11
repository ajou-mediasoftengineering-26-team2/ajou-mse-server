package team2.mse.ajou.server.domain.firebase;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * FRDB (Firebase Realtime DB) related constants. (e.g. Timestamp pattern/formats)
 *
 * @author Ahn Yubin / 202021088
 */
public final class FrdbConstants {
    /**
     * Timestamp formatting pattern used to format countdown timer to string.
     */
    public static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    /**
     * Shared ZoneId (constant) for setting ZonedDateTimes timezone.
     * This is important because if we don't use it, we'd get time formatted in UTC instead of KST. And that's a problem since client uses KST.
     */
    public final static ZoneId TIME_ZONE_ID = ZoneId.of("Asia/Seoul");
    /**
     * Shared time formatter.
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN).withZone(TIME_ZONE_ID);
}
