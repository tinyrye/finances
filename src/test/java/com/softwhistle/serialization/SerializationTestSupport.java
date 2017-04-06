package com.softwhistle.serialization;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class SerializationTestSupport
{
	protected ObjectMapper jsonObjectMapper = new ObjectMapper();

    public static <T,O> void forBothEach(Iterator<T> lane1, Iterator<O> lane2, BiConsumer<T,O> eachConsumer) {
        lane1.forEachRemaining(lane1Element -> eachConsumer.accept(lane1Element, lane2.next()));
    }

	protected <T> T readTestEntityFromJson(Class<T> entityClass, String testDataName) throws Exception {
        return jsonObjectMapper.reader(entityClass).readValue(streamForTestData(testDataName));
    }

    protected <T> Iterator<T> readTestEntitiesFromJson(Class<T> entityClass, String testDataName) throws Exception {
        return jsonObjectMapper.reader(entityClass).readValues(streamForTestData(testDataName));
    }

    protected InputStream streamForTestData(String testDataName) throws IOException {
        return getClass().getResourceAsStream(String.format("%s.%s.json", getClass().getSimpleName(), testDataName));
    }
}