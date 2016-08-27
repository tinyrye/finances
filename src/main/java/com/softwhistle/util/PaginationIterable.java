package com.softwhistle.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class PaginationIterable<T> implements Iterable<T>
{
    public static <T> PaginationIterable<T> of(Supplier<Integer> count, Function<LinearRange,Iterator<T>> pageOf) {
        return new PaginationIterable<T>() {
            @Override public int count() { return count.get(); }
            @Override public Iterator<T> pageOf(LinearRange page) { return pageOf.apply(page); }
        };
    }

    public static <T> PaginationIterable<T> of(Supplier<Integer> count, Function<LinearRange,Iterator<T>> pageOf, int pageSize) {
        return PaginationIterable.of(count, pageOf).pageSize(pageSize);
    }

    private int pageSize = 25;

    public int pageSize() { return pageSize; }
    public PaginationIterable<T> pageSize(int pageSize) { this.pageSize = pageSize; return this; }
    
    public abstract int count();
    protected abstract Iterator<T> pageOf(LinearRange page);

    @Override
    public final Iterator<T> iterator() {
        return new PaginationIterator();
    }
    
    protected class PaginationIterator implements Iterator<T>
    {
        private final int total;
        private LinearRange pageRange;
        private Iterator<T> page;

        public PaginationIterator() {
            total = count();
            pageRange = new LimitOffset().limit(pageSize()).offset(0);
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return (((page != null) && page.hasNext()) || (total >= pageRange.lower()));
        }

        @Override
        public T next() {
            if ((page == null) || ! page.hasNext()) loadPage();
            if (page != null) return page.next();
            else throw new NoSuchElementException();
        }

        protected void loadPage() {
            page = pageOf(pageRange);
            pageRange.increment();
        }
    }
}
