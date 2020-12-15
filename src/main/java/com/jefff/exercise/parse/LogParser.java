package com.jefff.exercise.parse;

import com.jefff.exercise.LogEntry;
import com.jefff.exercise.utility.TimeMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogParser {

    public static final int TIMESTAMP_PATTERN_LENGTH = TimeMapper.getPatternLength();
    public static final int OFFSET_OF_CLASS_NAME = TIMESTAMP_PATTERN_LENGTH + 1;
    public static final int MINIMUM_CLASS_NAME_LENGTH = 1;
    public static final String MSG_PADDING = " - ";
    public static final int MIN_LINE_LENGTH = OFFSET_OF_CLASS_NAME + MINIMUM_CLASS_NAME_LENGTH + MSG_PADDING.length();

    public LogParser() {
    }

    public LogEntry parseLogEntry(String line, int lineNumber) {
        int lineLength = line == null ? 0 : line.length();
        if (lineLength < MIN_LINE_LENGTH) {
            log.warn("Log line on lineNumber: {} has length {} which is less than the minimum of {}",
                     lineNumber, lineLength, MIN_LINE_LENGTH);
            return null;
        }
        @SuppressWarnings("ConstantConditions")
        String timestampStr = line.substring(0, TIMESTAMP_PATTERN_LENGTH);
        Long dateSeconds = TimeMapper.toDateSeconds(timestampStr);
        if (dateSeconds == null) {
            log.warn("Could not parse the timestamp: '{}' on line: {}",
                     timestampStr, lineNumber);
            return null;
        }
        if (line.charAt(TIMESTAMP_PATTERN_LENGTH) != ' ') {
            // should be a space here
            log.warn("Missing space at position: '{}' on line: {}",
                     TIMESTAMP_PATTERN_LENGTH, lineNumber);
            return null;
        }
        int endOfClassName = line.indexOf(' ', OFFSET_OF_CLASS_NAME);
        if (endOfClassName == -1) {
            log.warn("Missing space delimiter after className on line: {}",
                     lineNumber);
            return null;
        }
        String className = line.substring(OFFSET_OF_CLASS_NAME, endOfClassName);
        int startOfMsg = endOfClassName + MSG_PADDING.length();
        String preMsgPadding = startOfMsg > lineLength ? "" : line.substring(endOfClassName, startOfMsg);
        if (!MSG_PADDING.equals(preMsgPadding)) {
            log.warn("Missing hyphenated padding after className on line: {}",
                     lineNumber);
            return null;
        }
        String msg = line.substring(startOfMsg);
        LogEntry logEntry = new LogEntry(dateSeconds, className, msg);
        return logEntry;
    }

}
