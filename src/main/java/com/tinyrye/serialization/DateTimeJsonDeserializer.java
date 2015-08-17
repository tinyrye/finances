package com.tinyrye.serialization;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateTimeJsonDeserializer extends JsonDeserializer<OffsetDateTime>
{
    public OffsetDateTime deserialize(JsonParser parser, DeserializationContext objectParseContext)
        throws IOException, JsonProcessingException
    {
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return OffsetDateTime.parse(parser.getText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        else if (parser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            return OffsetDateTime.from(Instant.ofEpochMilli(parser.getLongValue()));
        }
        else {
            throw new IllegalStateException("Current JSON token must be a string or integer literal.");
        }
    }
}