package com.softwhistle.serialization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.junit.Test;

import com.softwhistle.model.FixedUnitOccurrences;

public class FixedUnitOccurrencesJsonDeserializerTest extends SerializationTestSupport
{
	@Test
	public void testMockDeserialize() throws Exception
	{
		JsonParser mockParser = mock(JsonParser.class);
		when(mockParser.getCurrentToken()).thenReturn(JsonToken.VALUE_STRING);
		when(mockParser.getText()).thenReturn("5 year");

		FixedUnitOccurrencesJsonDeserializer testedObject = new FixedUnitOccurrencesJsonDeserializer();
		FixedUnitOccurrences actualParsedObject = testedObject.deserialize(mockParser, null);

		assertNotNull(actualParsedObject);
		assertNotNull(actualParsedObject.magnitude);
		assertEquals(5, actualParsedObject.magnitude.intValue());
		assertEquals(ChronoUnit.YEARS, actualParsedObject.unit);
	}
}