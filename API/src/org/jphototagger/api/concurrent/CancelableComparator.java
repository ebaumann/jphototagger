package org.jphototagger.api.concurrent;

import java.util.Comparator;

/**
 * @author Elmar Baumann
 * @param <T>
 */
public final class CancelableComparator<T> implements Comparator<T>, Cancelable {

    private final Comparator<T> delegate;
    private volatile boolean cancel;

    public CancelableComparator(Comparator<T> delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate == null");
        }
        this.delegate = delegate;
    }

    @Override
    public int compare(T o1, T o2) {
        return cancel
                ? 0
                : delegate.compare(o1, o2);
    }

    /**
     * Does not throw an exception but does make
     * {@link #compare(java.lang.Object, java.lang.Object)} as fast as possible:
     * It returns 0.
     */
    @Override
    public void cancel() {
        cancel = true;
    }
}
