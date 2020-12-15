package com.jefff.exercise.utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeMapper {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static int getPatternLength() {
        return PATTERN.length();
    }

    public static Long toDateSeconds(String dateString) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, DATE_TIME_FORMATTER);
            long seconds = localDateTime.toEpochSecond(ZoneOffset.UTC);
            return seconds;
        } catch (Exception e) {
            return null;
        }
    }

    public static String toDateString(long seconds) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        String result = localDateTime.format(DATE_TIME_FORMATTER);
        return result;
    }
}
