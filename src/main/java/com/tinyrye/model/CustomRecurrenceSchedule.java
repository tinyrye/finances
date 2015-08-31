package com.tinyrye.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.OffsetDateTime;

public class CustomRecurrenceSchedule implements RecurrenceMethod
{
    public List<OffsetDateTime> occurences = new ArrayList<OffsetDateTime>();
    
    public CustomRecurrenceSchedule occurences(List<OffsetDateTime> occurences) { this.occurences = occurences; return this; }
    public CustomRecurrenceSchedule addOccurrence(OffsetDateTime occurrence) { occurences.add(occurrence); return this; }
    
    @Override
    public OffsetDateTime next(OffsetDateTime current) {
        int currentIndex = occurences.indexOf(current);
        if (currentIndex >= 0) return occurences.get(currentIndex + 1);
        else return null;
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (that instanceof CustomRecurrenceSchedule) return equals((CustomRecurrenceSchedule) that);
        else return false;
    }
    
    public boolean equals(CustomRecurrenceSchedule that) {
        return Objects.equals(this.occurences, that.occurences);
    }
}