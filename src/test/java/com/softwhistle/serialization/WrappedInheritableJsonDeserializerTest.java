package com.softwhistle.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

public class WrappedInheritableJsonDeserializerTest
{
    private static final Logger LOG = LoggerFactory.getLogger(WrappedInheritableJsonDeserializerTest.class);

    public static class TestEntity {
        public OccurrenceSchedule occurrence;
    }

    private static final CustomOccurrenceSchedule EXPECTED_CUSTOM_SCHEDULE = new CustomOccurrenceSchedule();
    static {
        try {
            EXPECTED_CUSTOM_SCHEDULE
                .addOccurrence(OffsetDateTime.parse("2015-01-01T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-01-18T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-02-07T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-02-18T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-02-28T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-03-05T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        } catch (Throwable ex) { LOG.error("Whoops", ex); }
    }
    
    private int assertionCount = 0;
    private ObjectMapper jsonObjectMapper;
    
    @Before
    public void reset()
    {
        assertionCount = 0;
        jsonObjectMapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(OccurrenceSchedule.class,
                WrappedInheritableJsonDeserializer.instanceForEntity(
                    OccurrenceSchedule.class)));
    }

    @Test @Ignore
    public void testDeserializeFixedIntervalStrings() throws Exception
    {
        forBothEach(
            readTestEntitiesFromJson("fixedUnitString"),
            Arrays.asList(
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
            readTestEntitiesFromJson("fixedUnitObject"),
            Arrays.asList(
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
                new FixedUnitOccurrences().unit(ChronoUnit.SECONDS).magnitude(34))
                    .iterator(),
            (actualObject, expectedObject) ->
                assertDeserialization(actualObject, expectedObject)
        );
    }
    
    @Test @Ignore
    public void testDeserializeCustomScheduleObject() throws Exception {
        assertDeserialization(readTestEntityFromJson("customScheduleObject"), EXPECTED_CUSTOM_SCHEDULE);
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
    
    protected TestEntity readTestEntityFromJson(String testDataName) throws Exception {
        return jsonObjectMapper.reader(TestEntity.class).readValue(streamForTestData(testDataName));
    }

    protected Iterator<TestEntity> readTestEntitiesFromJson(String testDataName) throws Exception {
        return jsonObjectMapper.reader(TestEntity.class).readValues(streamForTestData(testDataName));
    }

    protected InputStream streamForTestData(String testDataName) throws IOException {
        return getClass().getResourceAsStream(String.format("%s.%s.json", getClass().getSimpleName(), testDataName));
    }

    protected <T,O> void forBothEach(Iterator<T> lane1, Iterator<O> lane2, BiConsumer<T,O> eachConsumer) {
        lane1.forEachRemaining(lane1Element -> eachConsumer.accept(lane1Element, lane2.next()));
    }
}
