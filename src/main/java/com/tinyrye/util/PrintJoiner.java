package com.tinyrye.util;

import java.util.Iterator;
import java.util.function.Function;

public class PrintJoiner
{
    public static final String DEFAULT_DELIMITER = ",";
    public static final Function<Object,String> DEFAULT_VALUE_PRINTER = (obj) -> obj != null ? obj.toString() : null;
    
    private final String delimiter;
    
    public PrintJoiner() {
        this.delimiter = DEFAULT_DELIMITER;
    }

    public PrintJoiner(String delimiter) {
        this.delimiter = delimiter;
    }
    
    public <T> String print(Iterable<T> values) {
        return print(values, (Function<T,String>) DEFAULT_VALUE_PRINTER);
    }
    
    public <T> String print(Iterable<T> values, Function<T,String> printer) {
        final StringBuilder build = new StringBuilder();
        printTo(values, printer, build);
        return build.toString();
    }
    
    public <T> PrintJoiner printTo(Iterable<T> values, StringBuilder toAppendTo) {
        return printTo(values, (Function<T,String>) DEFAULT_VALUE_PRINTER, toAppendTo);
    }
    
    public <T> PrintJoiner printTo(Iterable<T> values, Function<T,String> printer, StringBuilder toAppendTo)
    {
        Iterator<T> valuesIterator = values.iterator();
        if (valuesIterator.hasNext()) {
            toAppendTo.append(printer.apply(valuesIterator.next()));
            valuesIterator.forEachRemaining(value -> toAppendTo.append(delimiter).append(printer.apply(value)));
        }
        return this;
    }
}