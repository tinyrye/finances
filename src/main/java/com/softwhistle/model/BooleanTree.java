package com.softwhistle.model;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BooleanTree<T extends Serializable>
{
    public static class BooleanNode<T extends Serializable> {
        public T value;
        public BooleanLevel<T> children;
        public BooleanNode<T> value(T value) { this.value = value; return this; }
        public BooleanNode<T> children(BooleanLevel<T> children) { this.children = children; return this; }
    }

    public static class BooleanLevel<T extends Serializable>
    {
        public List<BooleanNode<T>> nodes = new ArrayList<BooleanNode<T>>();
        public List<Boolean> connectors = new ArrayList<Boolean>();
        public static <T extends Serializable> BooleanLevel<T> of(Boolean connector, T ... values) {
            BooleanLevel<T> level = new BooleanLevel<T>();
            for (int i = 0; i < values.length - 1; i++) level.connectors.add(connector);
            level.nodes = asList(values).stream().map(value -> new BooleanNode<T>().value(value))
                .collect(Collectors.toList());
            return level;
        }
    }

    public static <T extends Serializable> BooleanTree<T> root(Boolean connector, T ... values) {
        BooleanTree<T> tree = new BooleanTree<T>();
        tree.root = new BooleanNode<T>().children(BooleanLevel.of(connector, values));
        return tree;
    }

    public BooleanNode<T> root;
}
