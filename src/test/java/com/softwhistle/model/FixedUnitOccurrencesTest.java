package com.softwhistle.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class FixedUnitOccurrencesTest
{
    @Test
    public void testOccurrences()
    {
        FixedUnitOccurrences testedObject = new FixedUnitOccurrences();
        testedObject.magnitude(1);
        testedObject.unit(ChronoUnit.MONTHS);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime end = now.plus(10, ChronoUnit.MONTHS);
        Iterator<OffsetDateTime> occurrences = testedObject.occurrences(now, end);
        for (OffsetDateTime cursor = now; ! cursor.isAfter(end); cursor = cursor.plus(1, ChronoUnit.MONTHS)) {
            Assert.assertTrue(occurrences.hasNext());
            Assert.assertEquals(cursor, occurrences.next());
        }
    }
}
