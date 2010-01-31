/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-26
 */
public final class CollectionUtil {

    /**
     * Inserts into an ascending sorted list an element.
     * 
     * Preconditions: The element has to implement the {@link Comparable}
     * interface and the list have to be sorted ascending. Both conditions will
     * not be checked: At runtime a class cast exception will be thrown
     * if the element does not implement the comparable interface and and if the
     * list is not sorted, the element can't be insert sorted.
     * 
     * @param <T>     element type
     * @param list
     * @param element
     */
    @SuppressWarnings("unchecked")
    public static <T> void binaryInsert(LinkedList<? super T> list, T element) {
            assert element instanceof Comparable<?>;

            int size  = list.size();
            int low   = 0;
            int high  = size - 1;
            int index = size;
            int cmp   = 1;

            while (low <= high && cmp > 0) {
                int                    mid    = (low + high) >>> 1;
                Comparable<? super T>  midVal = (Comparable<? super T>) list.get(mid);

                cmp = midVal.compareTo(element);

                if (cmp < 0) {
                    low   = mid + 1;
                } else if (cmp > 0) {
                    high = mid - 1;
                }
            }

            for (int i = low; i >= 0 && i < size && index == size; i++) {
                Comparable<? super T>  elt = (Comparable<? super T>) list.get(i);
                if (elt.compareTo(element) >= 0) {
                    index = i;
                }
            }

            list.add(index, element);
    }

    /**
     * Returns a list of strings from al collection of arbitrary objects.
     *
     * Uses the <code>toString()</code> operation of every collection element.
     * Null elements set to null in the list.
     *
     * @param  coll collection
     * @return      list of strings
     */
    public static List<String> toStringList(Collection<?> coll) {
        if (coll == null) throw new NullPointerException("coll == null");

        List<String> list = new ArrayList<String>(coll.size());

        for (Object o : coll) {
            if (o == null) {
                list.add(null);
            } else {
                list.add(o.toString());
            }
        }

        return list;
    }

    private CollectionUtil() {
    }
}
