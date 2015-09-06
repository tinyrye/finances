package com.tinyrye.serialization;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class JsonParsingSupport
{
    public <T> T readValue(JsonParser parser, DeserializationContext objectParseContext, Class<T> objectType) {
        try { return objectParseContext.readValue(parser, objectType); }
        catch (JsonProcessingException ex) { throw new RuntimeException(ex); }
        catch (IOException ex) { throw new RuntimeException(ex); }
    }

    public <T> T readFirstField(JsonParser parser, EntityInterpreter<T> firstFieldInterpreter)
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
    
    public <T> T readFieldsForWithFirstSniffed(
            JsonParser parser, String existingFieldName,
            T entity, FieldSetter<T> fieldSetter)
        throws IOException, JsonProcessingException
    {
        fieldSetter.setField(existingFieldName, entity, parser);
        return readFieldsFor(parser, entity, fieldSetter);
    }
    
    public <T> T readFieldsFor(JsonParser parser, T entity, FieldSetter<T> fieldSetter)
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
                    fieldSetter.setField(fieldName, entity, parser);
                }
            }
            else {
                throw newParseException("Expected field", parser);
            }
        }
        return entity;
    }
    
    public void readThroughObject(JsonParser parser) throws IOException, JsonProcessingException {
        while (acceptNextToken(parser, (nextToken) -> nextToken != JsonToken.END_OBJECT)) {
            /* no-op; iterations make sure current object is passed. */
        }
    }
    
    public boolean acceptNextToken(JsonParser parser, Predicate<JsonToken> tokenAcceptance) 
        throws IOException, JsonProcessingException
    {
        return ((parser.nextToken() != null) && tokenAcceptance.test(parser.getCurrentToken()));
    }
    
    /**
     * Normally would use java.util.Optional but throws clause orElseThrow(Supplier)
     * if using lambda for Supplier that returns newParseException causes compile error.
     * As shown:
     * <div>
     *   Optional.ofValue(parser.getText()).map(text -> SomeEnum.valueOf(text))
     *       .orElseThrow(() -> newParseException(parser, "Text is required"))
     * </div>
     * Java compiler doesn't interpret Supplier lambda's parameterized type as
     * JsonParseException due to the definition of orElseThrow's throws clause.
     */
    public <T,O> O mapOrThrow(JsonParser parser, T obj, Function<T,O> mapper, Supplier<String> message) throws JsonProcessingException {
        if (obj != null) return mapper.apply(obj);
        else throw newParseException(message.get(), parser);
    }
    
    public JsonParseException newParseException(String message, JsonParser parser) {
        return new JsonParseException(message, parser.getCurrentLocation());
    }
}