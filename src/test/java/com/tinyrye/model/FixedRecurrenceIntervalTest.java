package com.tinyrye.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;

public class FixedRecurrenceIntervalTest
{
    @Test
    public void testNext()
    {
        FixedRecurrenceInterval testedObject = new FixedRecurrenceInterval();
        testedObject.magnitude(1);
        testedObject.unit(ChronoUnit.MONTHS);

        Assert.assertEquals(
            OffsetDateTime.parse("2015-02-04T00:00:00+00:00"),
            testedObject.next(OffsetDateTime.parse("2015-01-04T00:00:00+00:00")));

        Assert.assertEquals(
            OffsetDateTime.parse("2015-03-04T00:00:00+00:00"),
            testedObject.next(OffsetDateTime.parse("2015-02-04T00:00:00+00:00")));

        Assert.assertEquals(
            OffsetDateTime.parse("2015-04-04T00:00:00+00:00"),
            testedObject.next(OffsetDateTime.parse("2015-03-04T00:00:00+00:00")));

        Assert.assertEquals(
            OffsetDateTime.parse("2015-05-04T00:00:00+00:00"),
            testedObject.next(OffsetDateTime.parse("2015-04-04T00:00:00+00:00")));

        Assert.assertEquals(
            OffsetDateTime.parse("2015-06-04T00:00:00+00:00"),
            testedObject.next(OffsetDateTime.parse("2015-05-04T00:00:00+00:00")));
    }
}