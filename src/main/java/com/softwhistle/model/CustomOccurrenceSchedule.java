package com.softwhistle.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.softwhistle.serialization.CustomOccurrenceScheduleJsonDeserializer;
import com.softwhistle.serialization.NamedType;

@NamedType("customSchedule")
@JsonDeserialize(using=CustomOccurrenceScheduleJsonDeserializer.class)
public class CustomOccurrenceSchedule implements OccurrenceSchedule
{
    public Integer id;
    public List<OffsetDateTime> occurrences = new ArrayList<OffsetDateTime>();
    
    public CustomOccurrenceSchedule id(Integer id) { this.id = id; return this; }
    public CustomOccurrenceSchedule occurrences(List<OffsetDateTime> occurrences) { this.occurrences = occurrences; return this; }
    public CustomOccurrenceSchedule addOccurrence(OffsetDateTime occurrence) { occurrences.add(occurrence); return this; }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public Iterator<OffsetDateTime> occurrences(OffsetDateTime from, OffsetDateTime to) {
        return occurrences.iterator();
    }
        
    @Override
    public String toString() {
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put("id", id);
        properties.put("occurrences", occurrences);
        return new ObjectPrinter().printProperties("customOccurrenceSchedule", properties);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        else if (that == null) return false;
        else if (that instanceof CustomOccurrenceSchedule) return equals((CustomOccurrenceSchedule) that);
        else return false;
    }
    
    public boolean equals(CustomOccurrenceSchedule that) {
        return Objects.equals(this.occurrences, that.occurrences);
    }
}
