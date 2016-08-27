package com.softwhistle.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.softwhistle.testing.IteratorAsserts;

public class FixedFieldOccurrencesTest
{
    @Test
    public void testOccurrences()
    {
        FixedFieldOccurrences testedObject = new FixedFieldOccurrences();
        testedObject.magnitude(3);
        testedObject.field(ChronoField.DAY_OF_MONTH);
        OffsetDateTime start = dateTime("2015-01-01T13:52:09+00:00");
        OffsetDateTime end = start.plus(12, ChronoUnit.MONTHS);
        Iterator<OffsetDateTime> occurrences = testedObject.occurrences(start, end);
        List<OffsetDateTime> expectedOccurrences = Arrays.asList(
            dateTime("2015-01-03T13:52:09+00:00"),
            dateTime("2015-02-03T13:52:09+00:00"),
            dateTime("2015-03-03T13:52:09+00:00"),
            dateTime("2015-04-03T13:52:09+00:00"),
            dateTime("2015-05-03T13:52:09+00:00"),
            dateTime("2015-06-03T13:52:09+00:00"),
            dateTime("2015-07-03T13:52:09+00:00"),
            dateTime("2015-08-03T13:52:09+00:00"),
            dateTime("2015-09-03T13:52:09+00:00"),
            dateTime("2015-10-03T13:52:09+00:00"),
            dateTime("2015-11-03T13:52:09+00:00"),
            dateTime("2015-12-03T13:52:09+00:00")
        );

        IteratorAsserts.assertEquals(expectedOccurrences.iterator(), occurrences);
    }

    protected OffsetDateTime dateTime(String dateTimeString) {
        return OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
