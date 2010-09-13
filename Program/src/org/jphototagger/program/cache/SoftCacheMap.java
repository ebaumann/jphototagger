/*
 * @(#)SoftCacheMap.java    Created on 2009-07-18
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.cache;

import java.io.File;

import java.lang.ref.SoftReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Map containing SoftReferences for dropping mapped-to stuff in case of
 * memory shortage.
 *
 * @param <C>
 * @author Martin Pohlack
 */
public class SoftCacheMap<C extends CacheIndirection> {
    private HashMap<File, SoftReference<C>> _map = new HashMap<File,
                                               SoftReference<C>>();
    private final int  MAX_ENTRIES;
    final WorkQueue<C> w;

    public SoftCacheMap(int maxEntries, WorkQueue<C> _w) {
        if (_w == null) {
            throw new NullPointerException("_w == null");
        }

        MAX_ENTRIES = maxEntries;
        w           = _w;
    }

    public C get(File k) {
        if (k == null) {
            throw new NullPointerException("k == null");
        }

        SoftReference<C> sr = _map.get(k);

        if (sr == null) {
            return null;
        }

        return sr.get();
    }

    public C put(File k, C v) {
        if (k == null) {
            throw new NullPointerException("k == null");
        }

        if (v == null) {
            throw new NullPointerException("v == null");
        }

        SoftReference<C> sr = _map.put(k, new SoftReference<C>(v));

        if (sr == null) {
            return null;
        }

        return sr.get();
    }

    public C remove(File k) {
        if (k == null) {
            throw new NullPointerException("k == null");
        }

        SoftReference<C> sr = _map.remove(k);

        if (sr == null) {
            return null;
        }

        return sr.get();
    }

    public void clear() {
        _map.clear();
    }

    public int size() {
        return _map.size();
    }

    public boolean containsKey(File k) {
        if (k == null) {
            throw new NullPointerException("k == null");
        }

        if (!_map.containsKey(k)) {
            return false;
        }

        return _map.get(k).get() != null;
    }

    public Set<File> keySet() {
        return _map.keySet();
    }

    public void maybeCleanupCache() {

        /*
         *  1. get EntrySet
         * 2. sort entries according to age in softref, empty softrefs
         *    first, using a sorted set
         * 3. iterate over first n elements and remove them from original
         *    cache
         */
        if (size() <= MAX_ENTRIES) {
            return;
        }

        NavigableSet<Entry<File, SoftReference<C>>> removes =
            new TreeSet<Entry<File, SoftReference<C>>>(
                new CacheIndirectionAgeComparator<C>());

        removes.addAll(_map.entrySet());

        Iterator<Entry<File, SoftReference<C>>> it = removes.iterator();
        Entry<File, SoftReference<C>>           e;
        C                                       ci;

        for (int index = 0; (index < MAX_ENTRIES / 10) && it.hasNext();
                index++) {
            e = it.next();

            if (e.getValue() == null) {
                _map.remove(e.getKey());

                continue;
            }

            ci = e.getValue().get();

            if (ci == null) {
                _map.remove(e.getKey());

                continue;
            }

            synchronized (ci) {

                // check if this image is probably in a prefetch queue and remove it
                if (ci.isEmpty() && (w != null)) {
                    w.remove(ci);
                }

                _map.remove(ci.file);
            }
        }
    }
}
