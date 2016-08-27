package com.softwhistle.util;

import java.io.Serializable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class Loader<T> implements Supplier<T>
{
    public static <T> Loader<T> of(Supplier<T> source) {
        return new Loader<T>(source);
    }
    
    private Supplier<T> source;
    private T resource;
    private Predicate<T> validator = (r) -> true;

    public Loader(Supplier<T> source) {
        this.source = source;
    }

    @Override
    public T get() {
        if (resource == null || ! validator.test(resource)) resource = source.get();
        return resource;
    }
    
    public Loader<T> invalidateWith(Predicate<T> validator) {
        this.validator = validator;
        return this;
    }
}
