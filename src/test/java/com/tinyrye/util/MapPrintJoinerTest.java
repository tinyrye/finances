package com.tinyrye.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class MapPrintJoinerTest
{
    @Test
    public void testPrint()
    {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put("x", "y");
        values.put("foo", "bar");
        values.put("yomama", 9000);
        Assert.assertEquals("foo=bar;x=y;yomama=9000", new MapPrintJoiner().print(values));
        Assert.assertEquals("foo=bar; x=y; yomama=9000", new MapPrintJoiner("; ").print(values));
        Assert.assertEquals("foo = bar; x = y; yomama = 9000", new MapPrintJoiner("; ", " = ").print(values));
    }
}