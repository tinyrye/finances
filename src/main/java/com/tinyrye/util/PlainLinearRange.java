package com.tinyrye.util;

public class PlainLinearRange implements LinearRange
{
    public Integer lower;
    public Integer upper;

    public PlainLinearRange lower(Integer lower) { this.lower = lower; return this; }
    public PlainLinearRange upper(Integer upper) { this.upper = upper; return this; }
    @Override public Integer lower() { return lower; }
    @Override public Integer upper() { return upper; }
    @Override public void increment() { }
    @Override public void decrement() {}
}