package com.softwhistle.util;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DateTimeFieldRecurrenceIterator implements Iterator<OffsetDateTime>
{
    /**
     * The basis of this iterator's spacing between dates.
     * The base reference date time is not the first
     */
    public static OffsetDateTime nthRangeRecurrence(
        OffsetDateTime reference, int recurrence, ChronoField fixedField,
        int fixedFieldMagnitude)
    {
        return reference.plus(recurrence, fixedField.getRangeUnit())
                    .with(fixedField, fixedFieldMagnitude);
    }

    private OffsetDateTime reference; // aka the initial date at or before the first recurrence.
    private OffsetDateTime max;

    private int recurrence = 0;
    private ChronoField fixedField;
    private int fixedFieldMagnitude;

    public DateTimeFieldRecurrenceIterator(OffsetDateTime reference, OffsetDateTime max,
        ChronoField fixedField, int fixedFieldMagnitude)
    {
        this.reference = reference;
        this.max = max;
        this.fixedField = fixedField;
        this.fixedFieldMagnitude = fixedFieldMagnitude;
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Checkmate");
    }

    @Override
    public boolean hasNext() {
        return ((max == null) || ! nthRangeRecurrence(reference, recurrence, fixedField, fixedFieldMagnitude).isAfter(max));
    }
    
    @Override
    public OffsetDateTime next()
    {
        if (hasNext()) {
            return nthRangeRecurrence(reference, recurrence++, fixedField, fixedFieldMagnitude);
        }
        else {
            throw new NoSuchElementException("Checkmate");
        }
    }
}
