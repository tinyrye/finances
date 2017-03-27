package com.softwhistle.serialization;

import static com.softwhistle.util.DateTimeParses.*;

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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;

import com.softwhistle.model.CustomOccurrenceSchedule;

public class CustomOccurrenceScheduleJsonDeserializer extends JsonDeserializer<CustomOccurrenceSchedule>
{
    private static List<OffsetDateTime> parseFromArray(JsonParser parser)
            throws IOException, JsonProcessingException
    {
        return ((List<String>) parser.readValueAs(List.class)).stream()
            .map(DATE_TIME_PARSER_FUNCTION)
            .collect(Collectors.<OffsetDateTime>toList());
    }

    private static final Function<String,OffsetDateTime> DATE_TIME_PARSER_FUNCTION =
        (dateString) -> parseFlexibleOffsetDateTime(dateString);
    
    private static final FieldSetter<CustomOccurrenceSchedule> FIELD_SETTER = (fieldName, entity, parser) -> {
        if (fieldName == "occurrences") {
            entity.occurrences = parseFromArray(parser);
        }
    };

    private final JsonParsingSupport scanHelper = new JsonParsingSupport();

    @Override
    public CustomOccurrenceSchedule deserialize(JsonParser parser,
        DeserializationContext objectParseContext)
            throws IOException, JsonProcessingException
    {
        switch (parser.getCurrentToken()) {
            case START_ARRAY: return new CustomOccurrenceSchedule().occurrences(parseFromArray(parser));
            case START_OBJECT: return scanHelper.readFieldsFor(parser, new CustomOccurrenceSchedule(), FIELD_SETTER);
            case VALUE_STRING: return new CustomOccurrenceSchedule().addOccurrence(parseFlexibleOffsetDateTime(parser.getText()));
        }
        throw scanHelper.newParseException("Require an array or object", parser);
    }
}
