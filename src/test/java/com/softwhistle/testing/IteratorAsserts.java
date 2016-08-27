package com.softwhistle.testing;

import java.util.Iterator;

import org.junit.Assert;

public class IteratorAsserts
{
    public static <T> void assertEquals(Iterator<T> expected, Iterator<T> actual) {
        while (expected.hasNext()) {
            Assert.assertTrue(actual.hasNext());
            Assert.assertEquals(expected.next(), actual.next());
        }
    }
}
