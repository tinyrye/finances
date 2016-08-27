package com.softwhistle.serialization;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
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

import com.softwhistle.model.FixedUnitOccurrences;
import com.softwhistle.util.PrintJoiner;

public class FixedUnitOccurrencesJsonDeserializer extends JsonDeserializer<FixedUnitOccurrences>
{
    private static final Logger LOG = LoggerFactory.getLogger(FixedUnitOccurrencesJsonDeserializer.class);

    private static final Pattern FIXED_INTERVAL_PATTERN =
        Pattern.compile("(\\d+)\\s*(" + new PrintJoiner("|").print(FixedUnitOccurrences.UNIT_BY_SUFFIX.keySet()) + ")");
    
    public static Optional<Matcher> durationTextMatchOptional(String text) {
        Matcher match = FIXED_INTERVAL_PATTERN.matcher(text);
        if (! match.matches()) match = null;
        return Optional.ofNullable(match);
    }
    
    private JsonParsingSupport scanHelper = new JsonParsingSupport();
    private DateTimeJsonDeserializer dateTimeDeserializer = new DateTimeJsonDeserializer();
    
    private FieldSetter<FixedUnitOccurrences> fieldSetter = (fieldName, entity, parser) -> {
        if (fieldName.equals("unit")) {
            entity.unit(FixedUnitOccurrences.unitBySuffix(parser.getText()));
        }
        else if (fieldName.equals("magnitude")) {
            entity.magnitude(parser.getValueAsInt());
        }
        else if (fieldName.equals("duration")) {
            parseDurationTextInto(parser, entity);
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
    public FixedUnitOccurrences deserialize(JsonParser parser,
            DeserializationContext objectParseContext)
        throws IOException, JsonProcessingException
    {
        switch (parser.getCurrentToken()) {
            case START_OBJECT: return scanHelper.readFieldsFor(parser, new FixedUnitOccurrences(), fieldSetter);
            case VALUE_STRING: return parseDurationText(parser);
        }
        throw scanHelper.newParseException("FixedUnitOccurrences JSON must be either an object or string", parser);
    }
    
    protected FixedUnitOccurrences parseDurationText(JsonParser parser)
        throws IOException, JsonProcessingException
    {
        FixedUnitOccurrences occurrence = new FixedUnitOccurrences();
        parseDurationTextInto(parser, occurrence);
        return occurrence;
    }
    
    protected void parseDurationTextInto(JsonParser parser, FixedUnitOccurrences occurrence)
        throws IOException, JsonProcessingException
    {
        final String fixedDurationString = parser.getText();
        final Matcher durationMatch = durationTextMatchOptional(parser.getText())
            .orElseThrow(() -> scanHelper.newParseException(String.format("Invalid fixed duration pattern: %s",
                fixedDurationString), parser));
        LOG.debug("Matching fixed duration string values: text={}; magnitude={}; unit={}", new Object[] { fixedDurationString, durationMatch.group(1), durationMatch.group(2) });
        occurrence.magnitude(new Integer(durationMatch.group(1)));
        occurrence.unit(FixedUnitOccurrences.unitBySuffix(durationMatch.group(2)));
    }
}
