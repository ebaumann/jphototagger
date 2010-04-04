/*
 * @(#)Content.java    Created on 2010-04-04
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.util;

import java.util.HashSet;
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
    private T                      content;
    private final Set<Listener<T>> listeners = new HashSet<Listener<T>>();

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

        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Returns the conent.
     *
     * @return
     */
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

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners(Type type) {
        synchronized (listeners) {
            for (Listener<T> listener : listeners) {
                if (type.equals(Type.ADDED)) {
                    listener.contentAdded(content);
                } else if (type.equals(Type.REMOVED)) {
                    listener.contentRemoved();
                }
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

        Logger.getLogger(getClass().getName()).log(Level.FINEST,
                         content.toString());
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
        public void contentAdded(T content);

        /**
         * Called if the content has been removed.
         */
        public void contentRemoved();
    }
}
