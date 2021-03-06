package com.softwhistle.serialization;

import static java.util.Arrays.asList;
import static com.softwhistle.util.DateTimeParses.parseFlexibleOffsetDateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.softwhistle.model.CustomOccurrenceSchedule;
import com.softwhistle.model.FixedUnitOccurrences;
import com.softwhistle.model.OccurrenceSchedule;

public class WrappedInheritableJsonDeserializerTest extends SerializationTestSupport
{
    private static final Logger LOG = LoggerFactory.getLogger(WrappedInheritableJsonDeserializerTest.class);

    public static class TestEntity {
        public OccurrenceSchedule occurrence;
    }

    private int assertionCount = 0;
    
    @Before
    public void reset()
    {
        assertionCount = 0;
        jsonObjectMapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(OccurrenceSchedule.class,
                WrappedInheritableJsonDeserializer.instanceForEntity(
                    OccurrenceSchedule.class)));
    }

    @Test
    public void testDeserializeFixedIntervalStrings() throws Exception
    {
        forBothEach(
            readTestEntitiesFromJson(TestEntity.class, "fixedUnitString"),
            asList(
                new FixedUnitOccurrences().unit(ChronoUnit.YEARS).magnitude(1),
                new FixedUnitOccurrences().unit(ChronoUnit.YEARS).magnitude(3),
                new FixedUnitOccurrences().unit(ChronoUnit.YEARS).magnitude(6),
                new FixedUnitOccurrences().unit(ChronoUnit.MONTHS).magnitude(2),
                new FixedUnitOccurrences().unit(ChronoUnit.MONTHS).magnitude(13),
                new FixedUnitOccurrences().unit(ChronoUnit.DAYS).magnitude(7),
                new FixedUnitOccurrences().unit(ChronoUnit.DAYS).magnitude(11),
                new FixedUnitOccurrences().unit(ChronoUnit.HOURS).magnitude(2),
                new FixedUnitOccurrences().unit(ChronoUnit.HOURS).magnitude(5),
                new FixedUnitOccurrences().unit(ChronoUnit.HOURS).magnitude(10),
                new FixedUnitOccurrences().unit(ChronoUnit.MINUTES).magnitude(23),
                new FixedUnitOccurrences().unit(ChronoUnit.MINUTES).magnitude(46),
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(5),
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(18),
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(34))
                    .iterator(),
            (actualObject, expectedObject) -> assertDeserialization(actualObject, expectedObject)
        );
    }

    @Test
    public void testDeserializeFixedIntervalObjects() throws Exception
    {
        forBothEach(
            readTestEntitiesFromJson(TestEntity.class, "fixedUnitObject"),
            asList(
                new FixedUnitOccurrences().unit(ChronoUnit.YEARS).magnitude(1),
                new FixedUnitOccurrences().unit(ChronoUnit.YEARS).magnitude(3),
                new FixedUnitOccurrences().unit(ChronoUnit.YEARS).magnitude(6)
                    .startsAt(OffsetDateTime.parse("2015-06-25T00:00:00+00:00",
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                new FixedUnitOccurrences().unit(ChronoUnit.MONTHS).magnitude(2),
                new FixedUnitOccurrences().unit(ChronoUnit.MONTHS).magnitude(13),
                new FixedUnitOccurrences().unit(ChronoUnit.DAYS).magnitude(7),
                new FixedUnitOccurrences().unit(ChronoUnit.DAYS).magnitude(11),
                new FixedUnitOccurrences().unit(ChronoUnit.HOURS).magnitude(2),
                new FixedUnitOccurrences().unit(ChronoUnit.HOURS).magnitude(5),
                new FixedUnitOccurrences().unit(ChronoUnit.HOURS).magnitude(10),
                new FixedUnitOccurrences().unit(ChronoUnit.MINUTES).magnitude(23),
                new FixedUnitOccurrences().unit(ChronoUnit.MINUTES).magnitude(46),
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(5),
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(18),
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(34)
            ).iterator(),
            (actualObject, expectedObject) ->
                assertDeserialization(actualObject, expectedObject)
        );
    }
    
    @Test
    public void testDeserializeCustomScheduleObject() throws Exception {
        forBothEach(
            readTestEntitiesFromJson(TestEntity.class, "customScheduleObject"),
            asList(
                new CustomOccurrenceSchedule()
                    .addOccurrence(OffsetDateTime.parse("2015-01-01T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .addOccurrence(OffsetDateTime.parse("2015-01-18T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .addOccurrence(OffsetDateTime.parse("2015-02-07T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .addOccurrence(OffsetDateTime.parse("2015-02-18T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .addOccurrence(OffsetDateTime.parse("2015-02-28T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .addOccurrence(OffsetDateTime.parse("2015-03-05T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                new CustomOccurrenceSchedule()
                    .addOccurrence(parseFlexibleOffsetDateTime("2017-01-09"))
                    .addOccurrence(OffsetDateTime.parse("2017-01-28T00:08:17+05:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .addOccurrence(OffsetDateTime.parse("2017-02-14T00:00:00Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME)),
                new CustomOccurrenceSchedule()
                    .addOccurrence(parseFlexibleOffsetDateTime("2017-02-19"))
            ).iterator(),
            (actualObject, expectedObject) -> assertDeserialization(actualObject, expectedObject)
        );
    }
    
    protected void assertDeserialization(TestEntity actualObject, OccurrenceSchedule expectedOccurrenceSchedule)
    {
        assertionCount++;
        Assert.assertNotNull(actualObject);
        Assert.assertNotNull(String.format("Whoops: expected=%s; assertionCount=%d", expectedOccurrenceSchedule, assertionCount), actualObject.occurrence);
        Assert.assertEquals(
            String.format("Whoops: assertionCount=%d", assertionCount),
            expectedOccurrenceSchedule, actualObject.occurrence);
    }
}
