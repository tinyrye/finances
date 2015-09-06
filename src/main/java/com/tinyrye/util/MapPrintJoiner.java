package com.tinyrye.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapPrintJoiner
{
    public static final String DEFAULT_ENTRY_DELIMITER = ";";
    public static final String DEFAULT_ASSIGNMENT_DELIMITER = "=";
    
    private final String entryDelimiter;
    private final String assignmentDelimiter;
    
    private Comparator<Map.Entry<String,?>> entriesSorter = (entry1, entry2) ->
        entry1.getKey().compareTo(entry2.getKey());

    private final BiFunction<String,Object,String> defaultEntryPrinter;
    
    public MapPrintJoiner() {
        this(DEFAULT_ENTRY_DELIMITER, DEFAULT_ASSIGNMENT_DELIMITER);
    }
    
    public MapPrintJoiner(String entryDelimiter) {
        this(entryDelimiter, DEFAULT_ASSIGNMENT_DELIMITER);
    }
    
    public MapPrintJoiner(String entryDelimiter, String assignmentDelimiter) {
        this.entryDelimiter = entryDelimiter;
        this.assignmentDelimiter = assignmentDelimiter;
        defaultEntryPrinter = (key, value) -> String.format("%s%s%s", key, assignmentDelimiter,
            PrintJoiner.DEFAULT_VALUE_PRINTER.apply(value));
    }
    
    public <T> String print(Map<String,T> values) {
        return print(values, (BiFunction<String,T,String>) defaultEntryPrinter);
    }
    
    public <T> String print(Map<String,T> values, Function<T,String> printer) {
        return print(values, (key, value) -> printer.apply(value));
    }

    public <T> String print(Map<String,T> values, BiFunction<String,T,String> printer) {
        StringBuilder build = new StringBuilder();
        printTo(values, printer, build);
        return build.toString();
    }
    
    public <T> MapPrintJoiner printTo(Map<String,T> values, StringBuilder toAppendTo) {
        return printTo(values, (BiFunction<String,T,String>) defaultEntryPrinter, toAppendTo);
    }

    public <T> MapPrintJoiner printTo(Map<String,T> values, Function<T,String> printer, StringBuilder toAppendTo) {
        return printTo(values, (key, value) -> printer.apply(value), toAppendTo);
    }

    public <T> MapPrintJoiner printTo(Map<String,T> values, BiFunction<String,T,String> printer, StringBuilder toAppendTo) {
        List<Map.Entry<String,T>> entriesSort = new ArrayList<Map.Entry<String,T>>(values.entrySet());
        Collections.sort(entriesSort, entriesSorter);
        Iterator<Map.Entry<String,T>> entriesIterator = entriesSort.iterator();
        if (entriesIterator.hasNext()) {
            printTo(entriesIterator.next(), printer, toAppendTo);
            entriesIterator.forEachRemaining(entry -> delimAndPrintTo(entry, printer, toAppendTo));
        }
        return this;
    }
    
    protected <T> void delimAndPrintTo(Map.Entry<String,T> entry, BiFunction<String,T,String> printer, StringBuilder toAppendTo) {
        toAppendTo.append(entryDelimiter).append(printer.apply(entry.getKey(), entry.getValue()));
    }
    
    protected <T> void printTo(Map.Entry<String,T> entry, BiFunction<String,T,String> printer, StringBuilder toAppendTo) {
        toAppendTo.append(printer.apply(entry.getKey(), entry.getValue()));
    }
}