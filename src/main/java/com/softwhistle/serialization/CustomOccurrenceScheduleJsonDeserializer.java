package com.softwhistle.serialization;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.softwhistle.model.CustomOccurrenceSchedule;

public class CustomOccurrenceScheduleJsonDeserializer extends JsonDeserializer<CustomOccurrenceSchedule>
{
    private static final Function<String,OffsetDateTime> DATE_TIME_PARSER_FUNCTION =
        (dateString) -> OffsetDateTime.parse(dateString,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    
    private final JsonParsingSupport scanHelper = new JsonParsingSupport();
    
    @Override
    public CustomOccurrenceSchedule deserialize(JsonParser parser,
        DeserializationContext objectParseContext)
            throws IOException, JsonProcessingException
    {
        switch (parser.getCurrentToken()) {
            case START_ARRAY: return parseFromArray(parser);
            case START_OBJECT: return objectParseContext.readValue(parser, CustomOccurrenceSchedule.class);
        }
        throw scanHelper.newParseException("Require an array or object", parser);
    }
    
    protected CustomOccurrenceSchedule parseFromArray(JsonParser parser)
            throws IOException, JsonProcessingException
    {
        return new CustomOccurrenceSchedule().occurrences(
                    ((List<String>) parser.readValueAs(List.class)).stream()
                        .map(DATE_TIME_PARSER_FUNCTION)
                        .collect(Collectors.<OffsetDateTime>toList()));
    }
}
