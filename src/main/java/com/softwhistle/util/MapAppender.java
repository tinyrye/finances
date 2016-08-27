package com.softwhistle.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MapAppender<K,T>
{
    private final Map<K,T> map;

    public MapAppender() {
        this.map = new HashMap<K,T>();
    }

    public MapAppender(Map<K,T> map) {
        this.map = map;
    }

    public MapAppender<K,T> add(K key, T value) {
        map.put(key, value);
        return this;
    }

    public MapAppender<K,T> with(Consumer<Map<K,T>> appender) {
        appender.accept(map);
        return this;
    }

    public Map<K,T> toMap() {
        return map;
    }
}
