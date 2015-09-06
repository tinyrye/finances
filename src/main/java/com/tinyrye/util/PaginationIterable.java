package com.tinyrye.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class PaginationIterable<T> implements Iterable<T>
{
    public abstract int count();
    protected abstract List<T> pageOf(LinearRange page);

    /**
     * Sensible batch size
     */
    protected int pageSize() { return 25; }
    
    @Override
    public Iterator<T> iterator() {
        return new PaginationIterator();
    }

    protected class PaginationIterator implements Iterator<T>
    {
        private final int total;
        private LinearRange currentWindow;
        private Iterator<T> page;

        public PaginationIterator() {
            total = count();
            currentWindow = new LimitOffset().limit(pageSize()).offset(0);
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return (page != null && page.hasNext()) || (total > currentWindow.upper());
        }

        @Override
        public T next()
        {
            if (! hasNext()) {
                throw new NoSuchElementException();
            }
            else {
                if (page == null || ! page.hasNext()) loadPage();
                if (page != null && page.hasNext()) return page.next();
                else throw new NoSuchElementException();
            }
        }

        protected void loadPage() {
            if (page != null) currentWindow.increment();
            page = pageOf(currentWindow).iterator();
        }
    }
}