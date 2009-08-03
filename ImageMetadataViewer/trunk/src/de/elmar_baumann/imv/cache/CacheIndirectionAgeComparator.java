package de.elmar_baumann.imv.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Comparator;
import java.util.Map.Entry;

/**
 *
 * @author Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-18
 */
public class CacheIndirectionAgeComparator<C extends CacheIndirection>
        implements Comparator<Entry<File, SoftReference<C>>> {

    @Override
    public int compare(Entry<File, SoftReference<C>> o1,
                       Entry<File, SoftReference<C>> o2) {
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

        return (t1 < t2 ? -1 : (t1 == t2 ? 0 : 1));
    }
}
