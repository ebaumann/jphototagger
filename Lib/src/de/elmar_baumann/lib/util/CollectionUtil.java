package de.elmar_baumann.lib.util;

import java.util.LinkedList;

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

    private CollectionUtil() {
    }
}
