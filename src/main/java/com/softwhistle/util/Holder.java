package com.softwhistle.util;

import java.util.function.Consumer;

public class Holder<T> implements Consumer<T>
{
    public T value;
    
    @Override
    public void accept(T value) {
        this.value = value;
    }
}
