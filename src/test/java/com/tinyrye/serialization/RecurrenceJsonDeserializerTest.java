package com.tinyrye.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.junit.Assert;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.tinyrye.model.CustomRecurrenceSchedule;
import com.tinyrye.model.FixedRecurrenceInterval;
import com.tinyrye.model.RecurrenceMethod;

public class RecurrenceJsonDeserializerTest
{
    private static final Logger LOG = LoggerFactory.getLogger(RecurrenceJsonDeserializerTest.class);

    public static class TestEntity {
        @JsonDeserialize(using=RecurrenceJsonDeserializer.class)
        public RecurrenceMethod recurrenceMethod;
    }

    private static final List<FixedRecurrenceInterval> EXPECTED_FIXED_INTERVAL_VALUES =
        Arrays.asList(
            new FixedRecurrenceInterval().unit(ChronoUnit.YEARS).magnitude(1),
            new FixedRecurrenceInterval().unit(ChronoUnit.YEARS).magnitude(3),
            new FixedRecurrenceInterval().unit(ChronoUnit.YEARS).magnitude(6),
            new FixedRecurrenceInterval().unit(ChronoUnit.MONTHS).magnitude(2),
            new FixedRecurrenceInterval().unit(ChronoUnit.MONTHS).magnitude(13),
            new FixedRecurrenceInterval().unit(ChronoUnit.DAYS).magnitude(7),
            new FixedRecurrenceInterval().unit(ChronoUnit.DAYS).magnitude(11),
            new FixedRecurrenceInterval().unit(ChronoUnit.HOURS).magnitude(2),
            new FixedRecurrenceInterval().unit(ChronoUnit.HOURS).magnitude(5),
            new FixedRecurrenceInterval().unit(ChronoUnit.HOURS).magnitude(10),
            new FixedRecurrenceInterval().unit(ChronoUnit.MINUTES).magnitude(23),
            new FixedRecurrenceInterval().unit(ChronoUnit.MINUTES).magnitude(46),
            new FixedRecurrenceInterval().unit(ChronoUnit.SECONDS).magnitude(5),
            new FixedRecurrenceInterval().unit(ChronoUnit.SECONDS).magnitude(18),
            new FixedRecurrenceInterval().unit(ChronoUnit.SECONDS).magnitude(34));
    
    private static final CustomRecurrenceSchedule EXPECTED_CUSTOM_SCHEDULE =
         new CustomRecurrenceSchedule();
    static {
        try {
            EXPECTED_CUSTOM_SCHEDULE
                .addOccurrence(OffsetDateTime.parse("2015-01-01T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-01-18T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-02-07T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-02-18T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-02-28T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .addOccurrence(OffsetDateTime.parse("2015-03-05T00:00:00+00:00", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        } catch (Throwable ex) {
            LOG.error("Whoops", ex);
        }
    }
    
    private final ObjectMapper jsonObjectMapper = new ObjectMapper();

    @Test
    public void testDeserializeFixedIntervalStrings() throws Exception {
        // the tested object, RecurrenceJsonDeserializer, is implicitly created by object mapper
        // via @JsonDeserialize annotation on TestEntity.recurrence field.
        forBothEach(
            readTestEntitiesFromJson("fixedIntervalString"), EXPECTED_FIXED_INTERVAL_VALUES.iterator(),
            (actualObject, expectedObject) -> assertDeserialization(actualObject, expectedObject));
    }

    @Test
    public void testDeserializeFixedIntervalObjects() throws Exception {
        Iterator<FixedRecurrenceInterval> expectedObjects = EXPECTED_FIXED_INTERVAL_VALUES.iterator();
        readTestEntitiesFromJson("fixedIntervalObjects").forEachRemaining(actualObject ->
            assertDeserialization(actualObject, expectedObjects.next()));
    }

    @Test
    public void testDeserializeCustomScheduleObject() throws Exception {
        assertDeserialization(readTestEntityFromJson("customScheduleObject"), EXPECTED_CUSTOM_SCHEDULE);
    }

    protected void assertDeserialization(TestEntity actualObject, RecurrenceMethod expectedRecurrenceMethod) {
        Assert.assertNotNull(actualObject);
        Assert.assertNotNull(actualObject.recurrenceMethod);
        Assert.assertEquals(expectedRecurrenceMethod, actualObject.recurrenceMethod);
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