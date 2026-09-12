package org.jsmart.zerocode.core.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {

    public static LocalDateTime parseLocalDateTime(String value) {
        try {
            return ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME)
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (DateTimeParseException ignored) {
        }

        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }
}
