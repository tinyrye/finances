package com.softwhistle.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;

@FunctionalInterface
public interface FieldSetter<T>
{
    void setField(String fieldName, T entity, JsonParser parser)
        throws IOException, JsonProcessingException;
}
