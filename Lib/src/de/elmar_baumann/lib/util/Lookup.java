/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lookup containing objects of interest.
 * <p>
 * Inspired by http://bits.netbeans.org/dev/javadoc/org-openide-util/org/openide/util/ServiceLookup.html.
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public class Lookup {
    private final Map<Class<?>, Collection<Object>> contentOfClass =
        new HashMap<Class<?>, Collection<Object>>();
    private final Map<Class<?>, Set<Listener>> listenersOfClass =
        new HashMap<Class<?>, Set<Listener>>();

    private enum Type { ADDED, REMOVED }

    /**
     * Adds a lookup listener.
     *
     * @param clazz    class of content to lookup for
     * @param listener listener
     */
    public void addListener(Class<?> clazz, Listener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (listenersOfClass) {
            Set<Listener> listeners = listenersOfClass.get(clazz);

            if (listeners == null) {
                listeners = new HashSet<Listener>();
                listenersOfClass.put(clazz, listeners);
            }

            synchronized (listeners) {
                listeners.add(listener);
            }
        }
    }

    /**
     * Removes a lookup listener.
     *
     * @param clazz    class of content to lookup for
     * @param listener listener
     */
    public void removeListener(Class<?> clazz, Listener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        synchronized (listenersOfClass) {
            Set<Listener> listeners = listenersOfClass.get(clazz);

            if (listeners != null) {
                synchronized (listeners) {
                    listeners.remove(listener);

                    if (listeners.isEmpty()) {
                        listenersOfClass.remove(clazz);
                    }
                }
            }
        }
    }

    private void notifyListeners(Class<?> clazz,
                                 Collection<? extends Object> content,
                                 Type type) {
        synchronized (listenersOfClass) {
            if (clazz.equals(Object.class)) {
                for (Class<?> c : listenersOfClass.keySet()) {
                    Set<Listener> listeners = listenersOfClass.get(c);

                    if (listeners != null) {
                        notifyListeners(content, listeners, type);
                    }
                }
            } else {
                Set<Listener> listeners = listenersOfClass.get(clazz);

                if (listeners != null) {
                    notifyListeners(content, listeners, type);
                }
            }
        }
    }

    private void notifyListeners(Collection<? extends Object> content,
                                 Set<Listener> listeners, Type type) {
        for (Listener listener : listeners) {
            if (type.equals(Type.ADDED)) {
                listener.contentAdded(content);
            } else if (type.equals(Type.REMOVED)) {
                listener.contentRemoved(content);
            }
        }
    }

    /**
     * Adds content.
     *
     * @param <T>     type of added content
     * @param clazz   class object of the content
     * @param content content
     */
    public <T> void add(Class<T> clazz, T content) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        add(clazz, new ArrayList<T>(Collections.singletonList(content)));
    }

    /**
     * Adds content.
     *
     * @param <T>     type of added content
     * @param clazz   class object of the content
     * @param content content
     */
    public <T> void add(Class<T> clazz, Collection<T> content) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        synchronized (contentOfClass) {
            Collection<Object> collection = contentOfClass.get(clazz);

            if (collection == null) {
                collection = new ArrayList<Object>();
            }

            synchronized (collection) {
                collection.addAll(content);
            }

            contentOfClass.put(clazz, collection);
            notifyListeners(clazz, new ArrayList<Object>(content), Type.ADDED);
        }
    }

    /**
     * Removes content.
     *
     * @param <T>     type of added content
     * @param clazz   class object of the content
     * @param content content
     */
    public <T> void remove(Class<T> clazz, T content) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        remove(clazz, new ArrayList<T>(Collections.singletonList(content)));
    }

    /**
     * Removes content.
     *
     * @param <T>     type of removed content
     * @param clazz   class object of the content
     * @param content content
     */
    public <T> void remove(Class<T> clazz, Collection<T> content) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        synchronized (contentOfClass) {
            Collection<Object> collection = contentOfClass.get(clazz);

            if (collection != null) {
                synchronized (collection) {
                    collection.removeAll(content);

                    if (collection.isEmpty()) {
                        contentOfClass.remove(clazz);
                    }
                }

                notifyListeners(clazz, new ArrayList<Object>(content),
                                Type.REMOVED);
            }
        }
    }

    /**
     * Returns all added objects.
     *
     * @param  <T>   type of the objects
     * @param  clazz class object of the objects
     * @return       objects or empty list if no object of that class has been
     *               added
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> lookupAll(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        List<T>       content    = new ArrayList<T>();
        Collection<?> collection = contentOfClass.get(clazz);

        if (collection != null) {
            synchronized (collection) {
                content.addAll((Collection<? extends T>) collection);
            }
        }

        return content;
    }

    /**
     * Returns the first of added objects.
     * <p>
     * Usage if it's clear, that only one object has been added.
     *
     * @param  <T>   type of the objects
     * @param  clazz class object of the objects
     * @return       first added object or null if no object of that class has
     *               been added
     */
    public <T> T lookup(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        List<T> content = lookupAll(clazz);

        return content.isEmpty()
               ? null
               : content.get(0);
    }

    /**
     * Removes the complete content.
     */
    public void removeAll() {
        synchronized (contentOfClass) {
            for (Class<?> clazz : contentOfClass.keySet()) {
                Collection<?> collection = contentOfClass.get(clazz);

                if (collection != null) {
                    contentOfClass.remove(clazz);
                    notifyListeners(clazz, collection, Type.REMOVED);
                }
            }
        }
    }

    /**
     * Listens for changes of content in this Lookup.
     */
    public interface Listener {

        /**
         * Called if content was added.
         *
         * @param content added content
         */
        public void contentAdded(Collection<? extends Object> content);

        /**
         * Called if content was removed.
         *
         * @param content removed content
         */
        public void contentRemoved(Collection<? extends Object> content);
    }
}
