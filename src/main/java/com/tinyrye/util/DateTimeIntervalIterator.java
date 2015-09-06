package com.tinyrye.util;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DateTimeIntervalIterator implements Iterator<OffsetDateTime>
{
    private final Integer intervalSize;
    private final ChronoUnit intervalSizeUnit;
    private final OffsetDateTime cursorWall;
    private OffsetDateTime cursor;
    private boolean visited = false;
    
    public DateTimeIntervalIterator(
        Integer intervalSize, ChronoUnit intervalSizeUnit,
        OffsetDateTime startsAt, OffsetDateTime endsAt)
    {
        this.intervalSize = intervalSize;
        this.intervalSizeUnit = intervalSizeUnit;
        cursor = startsAt;
        cursorWall = endsAt;
    }
    
    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Nope");
    }
    
    public boolean reachedEnd() {
        return reached(cursorWall);
    }
    
    @Override
    public boolean hasNext() {
        return ! reachedEnd();
    }
    
    @Override
    public OffsetDateTime next()
    {
        if (! hasNext()) {
            throw new NoSuchElementException("Passed the wall");
        }
        else if (! visited) {
            visited = true;
            return cursor; 
        }
        else {
            return (cursor = projectNext(cursor));
        }
    }
    
    public DateTimeIntervalIterator nextUntilReached(OffsetDateTime point) {
        while (! reachedEnd() && ! reached(point)) { next(); }
        return this;
    }
    
    private OffsetDateTime projectNext(OffsetDateTime point) {
        return point.plus(intervalSize, intervalSizeUnit);
    }
    
    private boolean reached(OffsetDateTime point) {
        return ! cursor.isBefore(point);
    }
}