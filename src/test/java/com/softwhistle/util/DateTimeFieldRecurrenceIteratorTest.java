package com.softwhistle.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class DateTimeFieldRecurrenceIteratorTest
{
    @Test
    public void testRangeUnit() {
        Assert.assertEquals(ChronoUnit.DAYS, ChronoField.HOUR_OF_DAY.getRangeUnit());
        Assert.assertEquals(ChronoUnit.MONTHS, ChronoField.DAY_OF_MONTH.getRangeUnit());
        Assert.assertEquals(ChronoUnit.YEARS, ChronoField.MONTH_OF_YEAR.getRangeUnit());
    }
    
    @Test
    public void testBaseUnit() {
        Assert.assertEquals(ChronoUnit.HOURS, ChronoField.HOUR_OF_DAY.getBaseUnit());
        Assert.assertEquals(ChronoUnit.DAYS, ChronoField.DAY_OF_MONTH.getBaseUnit());
        Assert.assertEquals(ChronoUnit.MONTHS, ChronoField.MONTH_OF_YEAR.getBaseUnit());
    }
    
    @Test
    public void testNthRangeRecurrence()
    {
        OffsetDateTime testDateTime = dateTimeFromYMD(2015, 1, 1);
        ChronoField testedField = ChronoField.DAY_OF_MONTH;
        int testMagnitude = 5; // 5th day of every month
        for (int recurrenceTestIteration = 0; recurrenceTestIteration < 11; recurrenceTestIteration++) {
            Assert.assertEquals(dateTimeFromYMD(
                2015, (1 + recurrenceTestIteration), 5),
            DateTimeFieldRecurrenceIterator.nthRangeRecurrence(testDateTime, recurrenceTestIteration, testedField, testMagnitude));
        }
        testDateTime = dateTimeFromYMD(2000, 1, 1);
        testedField = ChronoField.MONTH_OF_YEAR;
        testMagnitude = 3; // March of every year
        // presuming current era lasts more than hundred years 
        for (int recurrenceTestIteration = 0; recurrenceTestIteration < 100; recurrenceTestIteration++) {
            Assert.assertEquals(
                dateTimeFromYMD((2000 + recurrenceTestIteration), 3, 1),
                DateTimeFieldRecurrenceIterator.nthRangeRecurrence(testDateTime, recurrenceTestIteration, testedField, testMagnitude)
            );
        }
    }

    protected OffsetDateTime dateTimeFromYMD(int year, int month, int day) {
        return OffsetDateTime.of(year, month, day, 0, 0, 0, 0, ZoneOffset.UTC);
    }
}
