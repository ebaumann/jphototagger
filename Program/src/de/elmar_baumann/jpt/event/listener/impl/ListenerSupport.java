package de.elmar_baumann.jpt.event.listener.impl;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @param <T> listener type
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-12
 */
public class ListenerSupport<T> {

    protected final Set<T> listeners = new HashSet<T>();

    public void add(T listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void remove(T listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public Set<T> get() {
        synchronized (listeners) {
            return listeners;
        }
    }
}
