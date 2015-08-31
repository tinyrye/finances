package com.tinyrye.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class FixedRecurrenceInterval implements RecurrenceMethod
{
    public static ChronoUnit parseUnit(String text)
    {
        if (text.equals("y") || text.equals("yr") || text.equals("year")) {
            return ChronoUnit.YEARS;
        }
        else if (text.equals("mo") || text.equals("month")) {
            return ChronoUnit.MONTHS;
        }
        else if (text.equals("d") || text.equals("day")) {
            return ChronoUnit.DAYS;
        }
        else if (text.equals("h") || text.equals("hr") || text.equals("hour")) {
            return ChronoUnit.HOURS;
        }
        else if (text.equals("min") || text.equals("minute")) {
            return ChronoUnit.MINUTES;
        }
        else if (text.equals("s") || text.equals("sec") || text.equals("second")) {
            return ChronoUnit.SECONDS;
        }
        else {
            throw new IllegalArgumentException("Invalid unit text");
        }
    }

    public Integer magnitude;
    public ChronoUnit unit;
    
    public FixedRecurrenceInterval magnitude(Integer magnitude) { this.magnitude = magnitude; return this; }
    public FixedRecurrenceInterval unit(ChronoUnit unit) { this.unit = unit; return this; }
    
    @Override
    public OffsetDateTime next(OffsetDateTime current) {
        return current.plus(magnitude.longValue(), unit);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (that instanceof FixedRecurrenceInterval) return equals((FixedRecurrenceInterval) that);
        else return false;
    }

    public boolean equals(FixedRecurrenceInterval that) {
        return Objects.equals(this.magnitude, that.magnitude)
                    && Objects.equals(this.unit, that.unit);
    }
}