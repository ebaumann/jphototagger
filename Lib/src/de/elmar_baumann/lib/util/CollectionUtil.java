/*
 * @(#)CollectionUtil.java    2010-01-26
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

package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class CollectionUtil {
    private static final String EMPTY_STRING = "";

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

        while ((low <= high) && (cmp > 0)) {
            int                   mid    = (low + high) >>> 1;
            Comparable<? super T> midVal =
                (Comparable<? super T>) list.get(mid);

            cmp = midVal.compareTo(element);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            }
        }

        for (int i = low; (i >= 0) && (i < size) && (index == size); i++) {
            Comparable<? super T> elt = (Comparable<? super T>) list.get(i);

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
        if (coll == null) {
            throw new NullPointerException("coll == null");
        }

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

    /**
     * Creates a list of strings from a string within tokens. Empty tokens will
     * be omitted: If a string within tokens is <code>"a,,b,,c"</code> and the
     * delimiter string is <code>","</code>, the returned list of strings
     * contains the tree elements <code>"a", "b", "c"</code>.
     *
     * @param string    String within tokens
     * @param delimiter Delimiter that separates the tokens. Every character
     *                  of the delimiter string is a separate delimiter. If
     *                  the string within tokens is <code>"I,like:ice"</code>
     *                  and the delimiter string is <code>",:"</code>, the
     *                  returned list of strings contains the three elements
     *                  <code>"I", "like", "ice"</code>.
     * @return          List of strings
     */
    public static List<String> stringTokenToList(String string,
            String delimiter) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        List<String>    list      =
            new ArrayList<String>(tokenizer.countTokens());

        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }

        return list;
    }

    /**
     * Creates a list of integers from a string within tokens. Empty tokens will
     * be omitted: If a string within tokens is <code>"1,,2,,3"</code> and the
     * delimiter string is <code>","</code>, the returned list of integers
     * contains the tree elements <code>1, 2, 3</code>.
     *
     * <em>It is expected, that each token can be parsed as an integer or is
     * empty!</em>
     *
     * @param string    String within tokens parsable as integer
     * @param delimiter Delimiter between the integer tokens. Every character
     *                  of the delimiter string is a separate delimiter. If
     *                  the string within tokens is <code>"1,2:3"</code>
     *                  and the delimiter string is <code>",:"</code>, the
     *                  returned list of integers contains the three elements
     *                  <code>1, 2, 3</code>.
     * @return          list of integers
     * @throws          NumberFormatException if the string contains a not empty
     *                  token that can't parsed as an integer
     */
    public static List<Integer> integerTokenToList(String string,
            String delimiter) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        List<Integer>   integerList = new ArrayList<Integer>();
        StringTokenizer tokenizer   = new StringTokenizer(string, delimiter);

        while (tokenizer.hasMoreTokens()) {
            integerList.add(Integer.parseInt(tokenizer.nextToken()));
        }

        return integerList;
    }

    /**
     * Returns wheter an list index is in the range of valid indexes.
     *
     * @param  list   list
     * @param  index  index
     * @return true if the index is valid
     */
    public static boolean isValidIndex(List<?> list, int index) {
        return (index >= 0) && (index < list.size());
    }

    /**
     * Inserts into one collection all elements of another collection not
     * contained in that collection.
     * <p>
     * Uses {@link Collection#contains(java.lang.Object)} to compare elements.
     *
     * @param <T>    the collection's element type
     * @param src    source collection to get elements from
     * @param target target collection to put elements into
     */
    public static <T> void addNotContainedElements(Collection<? extends T> src,
            Collection<? super T> target) {
        if (src == target) {
            return;
        }

        for (T t : src) {
            if (!target.contains(t)) {
                target.add(t);
            }
        }
    }

    /**
     * Returns a token string from a collection. Uses {@link Object#toString()}
     * to get the collection elements strings.
     *
     * @param collection           collection
     * @param delimiter            delimiter
     * @param delimiterReplacement replacement for all delimiters contained in
     *                             a collection's element
     * @return                     token string
     */
    public static String toTokenString(Collection<? extends Object> collection,
                                       String delimiter,
                                       String delimiterReplacement) {
        StringBuilder tokenString = new StringBuilder();
        int           index       = 0;

        for (Object o : collection) {
            tokenString.append(((index++ == 0)
                                ? EMPTY_STRING
                                : delimiter));
            tokenString.append(o.toString().replace(delimiter,
                    delimiterReplacement));
        }

        return tokenString.toString();
    }

    private CollectionUtil() {}
}
