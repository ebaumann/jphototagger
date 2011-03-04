package org.jphototagger.lib.util;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Set;

/**
 * Content that can be set and removed. Notifies listeners on changes (settings
 * or removals).
 *
 * @param <T> type of the conent
 * @author    Elmar Baumann
 */
public class Content<T> {
    private T content;
    private final Set<Listener<T>> listeners = new CopyOnWriteArraySet<Listener<T>>();

    private enum Type { ADDED, REMOVED }

    /**
     * Adds a content listener.
     *
     * @param listener listener
     */
    public void addListener(Listener<T> listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.add(listener);
    }

    public synchronized T getContent() {
        return content;
    }

    public synchronized boolean isEmpty() {
        return content == null;
    }

    /**
     * Removes a content listener.
     *
     * @param listener listener
     */
    public void removeListener(Listener<T> listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        listeners.remove(listener);
    }

    private void notifyListeners(Type type) {
        for (Listener<T> listener : listeners) {
            if (type.equals(Type.ADDED)) {
                listener.contentAdded(content);
            } else if (type.equals(Type.REMOVED)) {
                listener.contentRemoved();
            }
        }
    }

    /**
     * Sets the content.
     *
     * @param content content, <em>not</em> null
     */
    public synchronized void set(T content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }

        Logger.getLogger(getClass().getName()).log(Level.FINEST, content.toString());
        this.content = content;
        notifyListeners(Type.ADDED);
    }

    /**
     * Removes the content.
     */
    public synchronized void remove() {
        content = null;
        Logger.getLogger(getClass().getName()).log(Level.FINEST, "Empty");
        notifyListeners(Type.REMOVED);
    }

    /**
     * Listens for changes of content in this Content.
     *
     * @param <T> type of the content
     */
    public interface Listener<T> {

        /**
         * Called if content was added.
         *
         * @param content added content
         */
        void contentAdded(T content);

        /**
         * Called if the content has been removed.
         */
        void contentRemoved();
    }
}
