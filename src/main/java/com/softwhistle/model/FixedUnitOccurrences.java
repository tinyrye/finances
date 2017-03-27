package com.softwhistle.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.softwhistle.serialization.DateTimeJsonSerializer;
import com.softwhistle.serialization.FixedUnitOccurrencesJsonDeserializer;
import com.softwhistle.serialization.NamedType;

import com.softwhistle.util.DateTimeIntervalIterator;

@NamedType("fixedUnit")
@JsonDeserialize(using=FixedUnitOccurrencesJsonDeserializer.class)
public class FixedUnitOccurrences implements OccurrenceSchedule
{
    public static final Map<ChronoUnit,List<String>> UNIT_SUFFIX_BY_UNIT = new HashMap<ChronoUnit,List<String>>();
    public static final Map<String,ChronoUnit> UNIT_BY_SUFFIX = new HashMap<String,ChronoUnit>();
    static {
        UNIT_SUFFIX_BY_UNIT.put(ChronoUnit.YEARS, Arrays.asList("y", "yr", "year", "years"));
        UNIT_SUFFIX_BY_UNIT.put(ChronoUnit.MONTHS, Arrays.asList("mo", "month", "months"));
        UNIT_SUFFIX_BY_UNIT.put(ChronoUnit.DAYS, Arrays.asList("d", "day", "days"));
        UNIT_SUFFIX_BY_UNIT.put(ChronoUnit.HOURS, Arrays.asList("h", "hr", "hour", "hours"));
        UNIT_SUFFIX_BY_UNIT.put(ChronoUnit.MINUTES, Arrays.asList("min", "minute", "minutes"));
        UNIT_SUFFIX_BY_UNIT.put(ChronoUnit.SECONDS, Arrays.asList("s", "sec", "second", "seconds"));
        UNIT_SUFFIX_BY_UNIT.forEach((unit, suffixes) -> suffixes.forEach(suffix -> UNIT_BY_SUFFIX.put(suffix, unit)));
    }
    
    public static ChronoUnit unitBySuffix(String text) {
        return UNIT_BY_SUFFIX.get(text.toLowerCase());
    }
    
    public Integer id;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime startsAt;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime endsAt;

    public Integer magnitude;
    public ChronoUnit unit;
    
    public FixedUnitOccurrences id(Integer id) { this.id = id; return this; }
    public FixedUnitOccurrences startsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; return this; }
    public FixedUnitOccurrences endsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; return this; }
    public FixedUnitOccurrences magnitude(Integer magnitude) { this.magnitude = magnitude; return this; }
    public FixedUnitOccurrences unit(ChronoUnit unit) { this.unit = unit; return this; }
    
    @Override
    public Integer id() {
        return id;
    }
    
    @Override
    public Iterator<OffsetDateTime> occurrences(OffsetDateTime from, OffsetDateTime to) {
        return new DateTimeIntervalIterator(from, to, magnitude, unit);
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (that instanceof FixedUnitOccurrences) return equals((FixedUnitOccurrences) that);
        else return false;
    }
 
    public boolean equals(FixedUnitOccurrences that) {
        return Objects.equals(this.startsAt, that.startsAt)
            && Objects.equals(this.endsAt, that.endsAt)
            && Objects.equals(this.magnitude, that.magnitude)
            && Objects.equals(this.unit, that.unit);
    }
}