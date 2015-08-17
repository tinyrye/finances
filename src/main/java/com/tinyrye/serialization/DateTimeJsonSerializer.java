package com.tinyrye.serialization;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateTimeJsonSerializer extends JsonSerializer<OffsetDateTime>
{
    public void serialize(OffsetDateTime value, JsonGenerator writer, SerializerProvider provider)
        throws IOException, JsonProcessingException
    {
        writer.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
    }
}