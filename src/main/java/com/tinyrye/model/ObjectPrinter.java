package com.tinyrye.model;

import java.util.Map;

import com.tinyrye.util.MapPrintJoiner;

public class ObjectPrinter
{
    public String printObjectProperties(String objectName, Map<String,Object> properties) {
        final StringBuilder build = new StringBuilder(objectName).append(":[");
        new MapPrintJoiner(", ", " = ").printTo(properties, build);
        build.append("]");
        return build.toString();
    }
}