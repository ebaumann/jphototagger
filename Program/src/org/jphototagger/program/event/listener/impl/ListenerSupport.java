package org.jphototagger.program.event.listener.impl;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

/**
 *
 *
 * @param <T> listener type
 * @author Elmar Baumann
 */
public class ListenerSupport<T> {
    protected final Set<T> listeners = new CopyOnWriteArraySet<T>();

    public void add(T listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.add(listener);
    }

    public void remove(T listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.remove(listener);
    }

    /**
     * Returns added listeners.
     * 
     * @return thread save set (that does not reflect added or removed
     *         listeners after calling this mehtod)
     */
    public Set<T> get() {
        return Collections.unmodifiableSet(listeners);
    }
}
