package com.softwhistle.util;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeIntervalIterator implements Iterator<OffsetDateTime>
{
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeIntervalIterator.class);

    private final Integer intervalSize;
    private final ChronoUnit intervalSizeUnit;
    private final OffsetDateTime cursorWall;
    private OffsetDateTime cursor;
    private boolean visited = false;
    
    public DateTimeIntervalIterator(
        OffsetDateTime startsAt, OffsetDateTime endsAt,
        Integer intervalSize, ChronoUnit intervalSizeUnit)
    {
        if (startsAt == null) throw new RuntimeException("Missing start date for date interval iteration");
        else if (endsAt == null) throw new RuntimeException("Missing end date for date interval iteration");
        else if (intervalSize == null) throw new RuntimeException("Missing interval size for date interval iteration");
        else if (intervalSize == 0) throw new RuntimeException("Interval size must be greater than zero for date interval iteration");
        else if (intervalSizeUnit == null) throw new RuntimeException("Missing interval unit for date interval iteration");
        cursor = startsAt;
        cursorWall = endsAt;
        this.intervalSize = intervalSize;
        this.intervalSizeUnit = intervalSizeUnit;
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
        LOG.info("Projecting next: point={}; intervalSize={}; intervalSizeUnit={}", new Object[] {
            point, intervalSize, intervalSizeUnit
        });
        return point.plus(intervalSize, intervalSizeUnit);
    }
    
    private boolean reached(OffsetDateTime point) {
        return ! cursor.isBefore(point);
    }
}
