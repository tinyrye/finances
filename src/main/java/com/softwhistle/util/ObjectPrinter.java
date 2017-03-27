package com.softwhistle.util;

import java.util.Map;
import java.util.function.Consumer;

public class ObjectPrinter
{
    public String printProperties(String objectName, Map<String,Object> properties) {
        final StringBuilder build = new StringBuilder(objectName).append(":[");
        new MapPrintJoiner(", ", " = ").printTo(properties, build);
        build.append("]");
        return build.toString();
    }

    public String printProperties(String objectName, Consumer<MapAppender<String,Object>> buildAppender) {
        MapAppender<String,Object> builder = new MapAppender<String,Object>();
        buildAppender.accept(builder);
        return printProperties(objectName, builder.toMap());
    }
}
