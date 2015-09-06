package com.tinyrye.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class FixedOccurrenceIntervalTest
{
    @Test
    public void testOccurrences()
    {
        FixedOccurrenceInterval testedObject = new FixedOccurrenceInterval();
        testedObject.magnitude(1);
        testedObject.unit(ChronoUnit.MONTHS);

        Iterator<OffsetDateTime> occurrences = testedObject.occurrences(OffsetDateTime.now());
    }
}