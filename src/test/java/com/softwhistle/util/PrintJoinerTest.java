package com.softwhistle.util;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class PrintJoinerTest
{
    @Test
    public void testPrint()
    {
        Assert.assertEquals("1,2,3", new PrintJoiner().print(Arrays.asList(1, 2, 3)));
        Assert.assertEquals("1, 2, 3", new PrintJoiner(", ").print(Arrays.asList(1, 2, 3)));
        Assert.assertEquals("foobar,foobar,foobar", new PrintJoiner()
            .print(Arrays.asList(1, 2, 3),
                (val) -> "foobar"));
    }
}
