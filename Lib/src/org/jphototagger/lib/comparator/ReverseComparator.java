package org.jphototagger.lib.comparator;

import java.util.Comparator;

/**
 * @author Elmar Baumann
 */
public final class ReverseComparator<T> implements Comparator<T> {

    private final Comparator<T> delegate;

    public ReverseComparator(Comparator<T> delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }

        this.delegate = delegate;
    }

    @Override
    public int compare(T o1, T o2) {
        return delegate.compare(o2, o1);
    }
}
