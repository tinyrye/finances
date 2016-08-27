package com.softwhistle.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;

public class DateTimeIntervalIteratorTest
{
    @Test
    public void testNext()
    {
        DateTimeIntervalIterator testedObject = new DateTimeIntervalIterator(
            dateTime(2015, 6, 1), dateTime(2015, 7, 1),
            1, ChronoUnit.DAYS);
        OffsetDateTime expectedCursor = dateTime(2015, 6, 1);
        OffsetDateTime expectedDayAfter = dateTime(2015, 7, 2);
        int dayCount = 1;
        while (expectedCursor.isBefore(expectedDayAfter)) {
            Assert.assertTrue(String.format("For expected date: %s", expectedCursor), testedObject.hasNext());
            Assert.assertEquals(String.format("On iteration #%d", dayCount), expectedCursor, testedObject.next());
            expectedCursor = expectedCursor.plus(1, ChronoUnit.DAYS);
            dayCount++;
        }
    }

    private OffsetDateTime dateTime(int year, int month, int day) {
        return OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.ofHours(0));
    }
}
