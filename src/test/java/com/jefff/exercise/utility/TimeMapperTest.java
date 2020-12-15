package com.jefff.exercise.utility;

import org.junit.Assert;
import org.junit.Test;

public class TimeMapperTest {

    @Test
    public void roundTripTest() {
        String str = "2020-04-01 10:10:09";
        Long seconds = TimeMapper.toDateSeconds(str);
        Assert.assertNotNull(seconds);
        String roundTrip = TimeMapper.toDateString(seconds);
        Assert.assertEquals(str, roundTrip);
    }

    @Test
    public void testBadParseReturnsNull() {
        String str = "2020-04-01 10:10:0x";
        Long seconds = TimeMapper.toDateSeconds(str);
        Assert.assertNull(seconds);
    }

}