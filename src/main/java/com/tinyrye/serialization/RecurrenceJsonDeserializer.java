package com.tinyrye.serialization;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.tinyrye.model.CustomRecurrenceSchedule;
import com.tinyrye.model.FixedRecurrenceInterval;
import com.tinyrye.model.RecurrenceMethod;

public class RecurrenceJsonDeserializer extends JsonDeserializer<RecurrenceMethod>
{
    private static final Logger LOG = LoggerFactory.getLogger(RecurrenceJsonDeserializer.class);
    
    private static final Pattern FIXED_INTERVAL_PATTERN = Pattern.compile("(\\d+)(y|yr|year|mo|month|d|day|h|hr|hour|min|minute|s|sec|second)");
    private static final Function<String,OffsetDateTime> DATE_TIME_PARSER_FUNCTION = (dateString) ->
        OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    
    @FunctionalInterface
    public static interface EntityInterpreter<T> {
        T sniffEntityFromField(String initialFieldName, JsonParser parser) throws IOException, JsonProcessingException;
    }
    
    @FunctionalInterface
    public static interface FieldDeliverer<T> {
        void setField(String fieldName, T entity, JsonParser parser) throws IOException, JsonProcessingException;
    }
    
    private final FieldDeliverer<FixedRecurrenceInterval> fixedIntervalFieldSetter = (fieldName, entity, parser) ->
    {
        if (fieldName.equals("unit")) {
            entity.unit(parseUnit(parser));
        }
        else if (fieldName.equals("magnitude")) {
            entity.magnitude(parseMagnitude(parser));
        }
    };

    private final EntityInterpreter<RecurrenceMethod> parseMethodObject = (initialFieldName, parser) ->
    {
        if (initialFieldName.equals("magnitude")) {
            return readFieldsForWithFirstSniffed(parser, initialFieldName, new FixedRecurrenceInterval(),
                fixedIntervalFieldSetter);
        }
        else if (initialFieldName.equals("unit")) {
            return readFieldsForWithFirstSniffed(parser, initialFieldName, new FixedRecurrenceInterval(),
                fixedIntervalFieldSetter);
        }
        else if (initialFieldName.equals("occurences")) {
            try { return parseCustomSchedule(parser); } finally { readThroughObject(parser); }
        }
        else {
            throw newParseException("Unknown field based on known methods of recurrences", parser);
        }
    };

    @Override
    public RecurrenceMethod deserialize(final JsonParser parser, DeserializationContext objectParseContext)
        throws IOException, JsonProcessingException
    {
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return parseFixedIntervalText(parser);
        }
        else if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            return readFirstField(parser, parseMethodObject);
        }
        else if (parser.getCurrentToken() == JsonToken.START_ARRAY) {
            return parseCustomSchedule(parser);
        }
        else {
            throw newParseException("Current JSON token must be a string or integer literal.", parser);
        }
    }
    
    protected CustomRecurrenceSchedule parseCustomSchedule(JsonParser parser) throws IOException, JsonProcessingException {
        return new CustomRecurrenceSchedule().occurences(parseOccurrences(parser));
    }
    
    protected List<OffsetDateTime> parseOccurrences(JsonParser parser) throws IOException, JsonProcessingException {
        return ((List<String>) parser.readValueAs(List.class)).stream()
            .map(DATE_TIME_PARSER_FUNCTION)
            .collect(Collectors.<OffsetDateTime>toList());
    }
    
    protected ChronoUnit parseUnit(JsonParser parser) throws IOException, JsonProcessingException {
        return FixedRecurrenceInterval.parseUnit(parser.getText());
    }

    protected Integer parseMagnitude(JsonParser parser) throws IOException, JsonProcessingException {
        return new Integer(parser.getIntValue());
    }
    
    protected FixedRecurrenceInterval parseFixedIntervalText(JsonParser parser) throws IOException, JsonProcessingException
    {
        return optionalMatch(FIXED_INTERVAL_PATTERN, parser.getText()).map(matcher ->
            new FixedRecurrenceInterval().magnitude(new Integer(matcher.group(1)))
                .unit(FixedRecurrenceInterval.parseUnit(matcher.group(2)))
        ).orElseThrow(() -> newParseException("Invalid fixed interval pattern", parser));
    }

    protected <T> T readFirstField(JsonParser parser, EntityInterpreter<T> firstFieldInterpreter)
        throws IOException, JsonProcessingException
    {
        if (acceptNextToken(parser, (nextToken) -> nextToken == JsonToken.FIELD_NAME))
        {
            String fieldName = parser.getParsingContext().getCurrentName();
            if (! acceptNextToken(parser, ((nextToken) -> nextToken != null))) {
                throw newParseException("End of stream.", parser);
            }
            else {
                return firstFieldInterpreter.sniffEntityFromField(fieldName, parser);
            }
        }
        else {
            throw newParseException("Expected field", parser);
        }
    }

    protected <T> T readFieldsForWithFirstSniffed(JsonParser parser, String existingFieldName, T entity, FieldDeliverer<T> fieldDeliverer)
        throws IOException, JsonProcessingException
    {
        fieldDeliverer.setField(existingFieldName, entity, parser);
        return readFieldsFor(parser, entity, fieldDeliverer);
    }
    
    protected <T> T readFieldsFor(JsonParser parser, T entity, FieldDeliverer<T> fieldDeliverer)
        throws IOException, JsonProcessingException
    {
        while (acceptNextToken(parser, (nextToken) -> nextToken != JsonToken.END_OBJECT))
        {
            if (parser.getCurrentToken() == JsonToken.FIELD_NAME)
            {
                String fieldName = parser.getParsingContext().getCurrentName();
                if (! acceptNextToken(parser, ((nextToken) -> nextToken != null))) {
                    throw newParseException("End of stream", parser);
                }
                else {
                    fieldDeliverer.setField(fieldName, entity, parser);
                }
            }
            else {
                throw newParseException("Expected field", parser);
            }
        }
        return entity;
    }

    protected void readThroughObject(JsonParser parser) throws IOException, JsonProcessingException {
        while (acceptNextToken(parser, (nextToken) -> nextToken != JsonToken.END_OBJECT)) {
            /* make sure current object is passed. */
        }
    }

    protected boolean acceptNextToken(JsonParser parser, Predicate<JsonToken> tokenAcceptance) throws IOException, JsonProcessingException {
        return ((parser.nextToken() != null) && tokenAcceptance.test(parser.getCurrentToken()));
    }
    
    protected Optional<Matcher> optionalMatch(Pattern pattern, String text) {
        Matcher match = pattern.matcher(text);
        return (match.matches() ? Optional.of(match) : Optional.empty());
    }
    
    public Optional<Matcher> optionalFixedIntervalPatternMatch(String text) {
        return optionalMatch(FIXED_INTERVAL_PATTERN, text);
    }

    protected JsonParseException newParseException(String message, JsonParser parser) {
        return new JsonParseException(message, parser.getCurrentLocation());
    }
}