/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
