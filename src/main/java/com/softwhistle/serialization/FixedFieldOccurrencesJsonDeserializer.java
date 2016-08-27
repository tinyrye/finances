package com.softwhistle.serialization;

import java.io.IOException;
import java.time.temporal.ChronoField;
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

import com.softwhistle.model.FixedFieldOccurrences;

public class FixedFieldOccurrencesJsonDeserializer extends JsonDeserializer<FixedFieldOccurrences>
{
    private static final Logger LOG = LoggerFactory.getLogger(FixedFieldOccurrencesJsonDeserializer.class);

    private static final Pattern FIXED_INTERVAL_PATTERN =
        Pattern.compile("(\\d+)\\s*(\\w+)");
    
    public static Optional<Matcher> recurrenceTextMatchOptional(String text) {
        Matcher match = FIXED_INTERVAL_PATTERN.matcher(text);
        if (! match.matches()) match = null;
        return Optional.ofNullable(match);
    }
    
    private JsonParsingSupport scanHelper = new JsonParsingSupport();
    private DateTimeJsonDeserializer dateTimeDeserializer = new DateTimeJsonDeserializer();
    
    private FieldSetter<FixedFieldOccurrences> fieldSetter = (fieldName, entity, parser) -> {
        if (fieldName.equals("field")) {
            entity.field(ChronoField.valueOf(parser.getText().toUpperCase()));
        }
        else if (fieldName.equals("magnitude")) {
            entity.magnitude(parser.getValueAsInt());
        }
        else if (fieldName.equals("recurrence")) {
            parseRecurrenceTextInto(parser, entity);
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
    public FixedFieldOccurrences deserialize(JsonParser parser,
            DeserializationContext objectParseContext)
        throws IOException, JsonProcessingException
    {
        switch (parser.getCurrentToken()) {
            case START_OBJECT: return scanHelper.readFieldsFor(parser, new FixedFieldOccurrences(), fieldSetter);
            case VALUE_STRING: return parseRecurrenceText(parser);
        }
        throw scanHelper.newParseException("FixedFieldOccurrences JSON must be either an object or string", parser);
    }
    
    protected FixedFieldOccurrences parseRecurrenceText(JsonParser parser)
        throws IOException, JsonProcessingException
    {
        FixedFieldOccurrences occurrence = new FixedFieldOccurrences();
        parseRecurrenceTextInto(parser, occurrence);
        return occurrence;
    }
    
    protected void parseRecurrenceTextInto(JsonParser parser, FixedFieldOccurrences occurrence)
        throws IOException, JsonProcessingException
    {
        final String fixedRecurrenceString = parser.getText();
        final Matcher recurrenceMatch = recurrenceTextMatchOptional(parser.getText())
            .orElseThrow(() -> scanHelper.newParseException(String.format("Invalid fixed Recurrence pattern: %s",
                fixedRecurrenceString), parser));
        LOG.debug("Matching fixed Recurrence string values: text={}; magnitude={}; field={}", new Object[] { fixedRecurrenceString, recurrenceMatch.group(1), recurrenceMatch.group(2) });
        occurrence.magnitude(new Integer(recurrenceMatch.group(1)));
        occurrence.field(ChronoField.valueOf(recurrenceMatch.group(2).toUpperCase()));
    }
}
