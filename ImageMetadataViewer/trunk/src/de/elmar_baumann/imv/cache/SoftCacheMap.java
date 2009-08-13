package de.elmar_baumann.imv.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 *
 * @param <C> 
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class SoftCacheMap<C extends CacheIndirection> {
    HashMap<File, SoftReference<C>> _map =
            new HashMap<File, SoftReference<C>>();
    private final int MAX_ENTRIES;
    final WorkQueue w;

    public SoftCacheMap(int maxEntries, WorkQueue _w) {
        MAX_ENTRIES = maxEntries;
        w = _w;
    }

    C get(File k) {
        SoftReference<C> sr = _map.get(k);
        if (sr == null) {
            return null;
        }
        return sr.get();
    }

    C put(File k, C v) {
        SoftReference<C> sr = _map.put(k, new SoftReference<C>(v));
        if (sr == null) {
            return null;
        }
        return sr.get();
    }

    C remove(File k) {
        SoftReference<C> sr = _map.remove(k);
        if (sr == null) {
            return null;
        }
        return sr.get();
    }

    int size() {
        return _map.size();
    }

    boolean containsKey(File k) {
        if (! _map.containsKey(k)) {
            return false;
        }
        return _map.get(k).get() != null;
    }

    public void maybeCleanupCache() {
        /* 1. get EntrySet
         * 2. sort entries according to age in softref, empty softrefs
         *    first, using a sorted set
         * 3. iterate over first n elements and remove them from original
         *    cache
         */
        if (size() <= MAX_ENTRIES) {
            return;
        }

        NavigableSet<Entry<File, SoftReference<C>>> removes =
                new TreeSet<Entry<File, SoftReference<C>>>
                (new CacheIndirectionAgeComparator<C>());
        removes.addAll(_map.entrySet());
        Iterator<Entry<File, SoftReference<C>>> it = removes.iterator();

        Entry<File, SoftReference<C>> e;
        C ci;
        for (int index = 0;
             index < MAX_ENTRIES / 10 && it.hasNext();
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
            synchronized(ci) {
                // check if this image is probably in a prefetch queue and remove it
                if (ci.isEmpty()) {
                    w.remove(ci.file);
                }
                _map.remove(ci.file);
            }
        }
    }
}