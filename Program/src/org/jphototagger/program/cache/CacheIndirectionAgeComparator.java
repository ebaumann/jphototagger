package org.jphototagger.program.cache;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Comparator;
import java.util.Map.Entry;

/**
 * Comparator for sorting cache-indirection objects based on their age,
 * usefull for cache-replacement strategies etc.
 *
 * @param <C>
 * @author Martin Pohlack
 */
public class CacheIndirectionAgeComparator<C extends CacheIndirection>
        implements Comparator<Entry<File, SoftReference<C>>>, Serializable {
    private static final long serialVersionUID = 712279209565326209L;

    @Override
    public int compare(Entry<File, SoftReference<C>> o1, Entry<File, SoftReference<C>> o2) {
        C c;
        int t1, t2;

        c = o1.getValue().get();

        if (c == null) {
            t1 = 0;
        } else {
            t1 = c.usageTime;
        }

        c = o2.getValue().get();

        if (c == null) {
            t2 = 0;
        } else {
            t2 = c.usageTime;
        }

        return ((t1 < t2)
                ? -1
                : ((t1 == t2)
                   ? 0
                   : 1));
    }
}
