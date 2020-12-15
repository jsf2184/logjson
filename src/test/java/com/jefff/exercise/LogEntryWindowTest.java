package com.jefff.exercise;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogEntryWindowTest {


    /**
     * validateAddScenario() - do a series of additions to LogEntries validating periodicaly that the
     * window contains the timestamps that we'd expect.
     */
    @Test
    public void validateAddScenario() {
        LogEntryWindow window = new LogEntryWindow();
        List<Long> timestamps = window.getTimestamps();
        Assert.assertEquals(0, timestamps.size());

        window.add(new LogEntry(1, "C", "msg"), 1);
        timestamps = window.getTimestamps();
        Assert.assertEquals(Collections.singletonList(1L), timestamps);
        window.add(new LogEntry(2, "C", "msg"), 2);
        window.add(new LogEntry(3, "C", "msg"), 3);
        window.add(new LogEntry(3, "C", "msg"), 4);
        window.add(new LogEntry(3, "C", "msg"), 5);
        window.add(new LogEntry(4, "C", "msg"), 6);
        window.add(new LogEntry(5, "C", "msg"), 7);
        window.add(new LogEntry(6, "C", "msg"), 8);
        timestamps = window.getTimestamps();
        Assert.assertEquals(Arrays.asList(1L, 2L, 3L, 3L, 3L, 4L, 5L, 6L), timestamps);
        window.add(new LogEntry(7, "C", "msg"), 9);
        timestamps = window.getTimestamps();
        Assert.assertEquals(Arrays.asList(2L, 3L, 3L, 3L, 4L, 5L, 6L, 7L), timestamps);
        window.add(new LogEntry(8, "C", "msg"), 10);
        timestamps = window.getTimestamps();
        Assert.assertEquals(Arrays.asList(3L, 3L, 3L, 4L, 5L, 6L, 7L, 8L), timestamps);
        window.add(new LogEntry(9, "C", "msg"), 11);
        timestamps = window.getTimestamps();
        Assert.assertEquals(Arrays.asList(4L, 5L, 6L, 7L, 8L, 9L), timestamps);
        window.add(new LogEntry(15, "C", "msg"), 12);
        timestamps = window.getTimestamps();
        Assert.assertEquals(Collections.singletonList(15L), timestamps);
    }
}