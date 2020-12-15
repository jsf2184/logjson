package com.jefff.exercise;


import com.jefff.exercise.utility.TimeMapper;

public class LogEntry {
    long timestamp;
    String className;
    String message;

    public LogEntry(long timestamp, String className, String message) {
        this.timestamp = timestamp;
        this.className = className;
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimestampString() {
        return TimeMapper.toDateString(timestamp);
    }

    public String getClassName() {
        return className;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogEntry)) return false;

        LogEntry logEntry = (LogEntry) o;

        if (timestamp != logEntry.timestamp) return false;
        if (!className.equals(logEntry.className)) return false;
        return message.equals(logEntry.message);
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + className.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }
}
