package com.softwhistle.model;

import java.time.OffsetDateTime;
import java.util.Iterator;

public class OccurrenceScheduleIdHolder implements OccurrenceSchedule
{
    public Integer id;

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public Iterator<OffsetDateTime> occurrences(OffsetDateTime from, OffsetDateTime to) {
        throw new UnsupportedOperationException();
    }

    public OccurrenceScheduleIdHolder id(Integer id) { this.id = id; return this; }
}
