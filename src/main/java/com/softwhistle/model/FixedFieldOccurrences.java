package com.softwhistle.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
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
import com.softwhistle.serialization.FixedFieldOccurrencesJsonDeserializer;
import com.softwhistle.serialization.NamedType;
import com.softwhistle.util.DateTimeFieldRecurrenceIterator;

@NamedType("fixedField")
@JsonDeserialize(using=FixedFieldOccurrencesJsonDeserializer.class)
public class FixedFieldOccurrences implements OccurrenceSchedule
{
    private static final List<ChronoField> ACCEPTABLE_FIELDS_AND_ORDER = Arrays.asList(
        ChronoField.YEAR_OF_ERA,
        ChronoField.MONTH_OF_YEAR,
        ChronoField.DAY_OF_MONTH,
        ChronoField.HOUR_OF_DAY,
        ChronoField.MINUTE_OF_HOUR
    );
    
    public static boolean acceptable(ChronoField field) {
        return ACCEPTABLE_FIELDS_AND_ORDER.contains(field);
    }
    
    public Integer id;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime startsAt;
    
    @JsonSerialize(using=DateTimeJsonSerializer.class)
    public OffsetDateTime endsAt;

    public Integer magnitude;
    public ChronoField field;
    
    public FixedFieldOccurrences id(Integer id) { this.id = id; return this; }
    public FixedFieldOccurrences startsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; return this; }
    public FixedFieldOccurrences endsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; return this; }
    public FixedFieldOccurrences magnitude(Integer magnitude) { this.magnitude = magnitude; return this; }
    
    @Override
    public Integer id() {
        return id;
    }
    
    @Override
    public Iterator<OffsetDateTime> occurrences(OffsetDateTime from, OffsetDateTime to) {
        return new DateTimeFieldRecurrenceIterator(from, to, field, magnitude);
    }
    
    public FixedFieldOccurrences field(ChronoField field) {
        if (! acceptable(field)) throw new IllegalArgumentException("FixedFieldOccurrences.UNACCEPTABLE_CHRONO_FIELD");
        this.field = field; return this;
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (that instanceof FixedFieldOccurrences) return equals((FixedFieldOccurrences) that);
        else return false;
    }

    public boolean equals(FixedFieldOccurrences that) {
        return Objects.equals(this.startsAt, that.startsAt)
            && Objects.equals(this.endsAt, that.endsAt)
            && Objects.equals(this.magnitude, that.magnitude)
            && Objects.equals(this.field, that.field);
    }
}
