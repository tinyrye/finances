package com.softwhistle.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;

@FunctionalInterface
public interface EntityInterpreter<T>
{
    T sniffEntityFromField(String initialFieldName, JsonParser parser)
        throws IOException, JsonProcessingException;
}
