package com.tinyrye.serialization;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.type.SimpleType;

import com.tinyrye.model.FixedOccurrenceInterval;
import com.tinyrye.util.PrintJoiner;

public class FixedOccurrenceIntervalJsonDeserializer extends JsonDeserializer<FixedOccurrenceInterval>
{
    private static final Logger LOG = LoggerFactory.getLogger(FixedOccurrenceIntervalJsonDeserializer.class);

    private static final Pattern FIXED_INTERVAL_PATTERN =
        Pattern.compile("(\\d+)(" + new PrintJoiner("|").print(FixedOccurrenceInterval.UNIT_BY_SUFFIX.keySet()) + ")");
    
    public static Optional<Matcher> optionalFixedIntervalPatternMatch(String text) {
        Matcher match = FIXED_INTERVAL_PATTERN.matcher(text);
        if (! match.matches()) match = null;
        return Optional.ofNullable(match);
    }
    
    private JsonParsingSupport scanHelper = new JsonParsingSupport();
    private DateTimeJsonDeserializer dateTimeDeserializer = new DateTimeJsonDeserializer();

    private FieldSetter<FixedOccurrenceInterval> fieldSetter = (fieldName, entity, parser) -> {
        if (fieldName.equals("unit")) {
            entity.unit(FixedOccurrenceInterval.unitBySuffix(parser.getText()));
        }
        else if (fieldName.equals("magnitude")) {
            entity.magnitude(parser.getValueAsInt());
        }
        else if (fieldName.equals("startsAt")) {
            entity.startsAt(dateTimeDeserializer.deserialize(parser));
        }
        else if (fieldName.equals("endsAt")) {
            entity.endsAt(dateTimeDeserializer.deserialize(parser));
        }
        else if (fieldName.equals("id")) {
            entity.id(parser.getValueAsInt());
        }
    };

    @Override
    public FixedOccurrenceInterval deserialize(JsonParser parser,
            DeserializationContext objectParseContext)
                throws IOException, JsonProcessingException
    {
        switch (parser.getCurrentToken()) {
            case START_OBJECT: return scanHelper.readFieldsFor(parser, new FixedOccurrenceInterval(), fieldSetter);
            case VALUE_STRING: try { return parseFixedIntervalText(parser); } finally { scanHelper.readThroughObject(parser); }
        }
        throw scanHelper.newParseException("FixedOccurrenceInterval JSON must be either an object or string", parser);
    }

    protected FixedOccurrenceInterval parseFixedIntervalText(JsonParser parser)
            throws IOException, JsonProcessingException
    {
        final String fixedIntervalString = parser.getText();
        return optionalFixedIntervalPatternMatch(parser.getText())
            .map((matcher) -> {
                LOG.debug("Matching fixed interval string values: text={}; unit={}", new Object[] { fixedIntervalString, matcher.group(2) });
                return new FixedOccurrenceInterval().magnitude(new Integer(matcher.group(1)))
                    .unit(FixedOccurrenceInterval.unitBySuffix(matcher.group(2)));
            }).orElseThrow(() -> {
            LOG.warn("Invalid fixed interval string: {}", fixedIntervalString);
            return scanHelper.newParseException(String.format("Invalid fixed interval pattern: %s", fixedIntervalString), parser);
        });
    }
}