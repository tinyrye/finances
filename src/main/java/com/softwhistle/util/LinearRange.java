package com.softwhistle.util;

public interface LinearRange
{
    Integer lower();
    Integer upper();
    void increment();
    void decrement();

    default LinearRange intersect(LinearRange that) {
        return new PlainLinearRange()
                .lower(Math.max(this.lower(), that.lower()))
                .upper(Math.min(this.upper(), that.upper()));
    }

    default LinearRange boundLower(Integer lowerBound) {
        return intersect(new PlainLinearRange().lower(lowerBound).upper(this.upper()));
    }

    default LinearRange boundUpper(Integer upperBound) {
        return intersect(new PlainLinearRange().lower(this.lower()).upper(upperBound));
    }
}
