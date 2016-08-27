package com.softwhistle.util;

public class LimitOffset implements LinearRange
{
    public Integer limit;
    public Integer offset;

    public LimitOffset limit(Integer limit) { this.limit = limit; return this; }
    public LimitOffset offset(Integer offset) { this.offset = offset; return this; }
    @Override public Integer lower() { return offset; }
    @Override public Integer upper() { return offset + limit; }
    @Override public void increment() { offset += limit; }
    @Override public void decrement() { offset -= limit; }
}
