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

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utils for arrays and array like objects.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ArrayUtil {

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
    public static List<String> stringTokenToList(String string, String delimiter) {
        if (string == null)
            throw new NullPointerException("string == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        List<String> list = new ArrayList<String>(tokenizer.countTokens());
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
        if (string == null)
            throw new NullPointerException("string == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        List<Integer> integerList = new ArrayList<Integer>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
        while (tokenizer.hasMoreTokens()) {
            integerList.add(Integer.parseInt(tokenizer.nextToken()));
        }
        return integerList;
    }

    /**
     * Converts an array of objects into an array of strings. The strings are
     * created with the object's <code>toString()</code> method.
     * 
     * @param  objectArray array of objects
     * @return array of strings
     * @throws IllegalArgumentException if an element in the array of objects
     *         is null
     */
    public static String[] toStringArray(Object[] objectArray) {
        if (objectArray == null)
            throw new NullPointerException("array == null"); // NOI18N

        String[] stringArray = new String[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            if (objectArray[i] == null)
                throw new IllegalArgumentException(
                        "Element with index " + i + " is not an object: " + // NOI18N
                        objectArray[i] + " (" + objectArray.toString() + ")"); // NOI18N

            Object object = objectArray[i];
            stringArray[i] = object.toString(); // NOI18N
        }
        return stringArray;
    }

    /**
     * Returns wheter an list index is in the range of valid indexes.
     *
     * @param  list   list
     * @param  index  index
     * @return true if the index is valid
     */
    public static boolean isValidIndex(List list, int index) {
        return index >= 0 && index < list.size();
    }

    /**
     * Returns an array of (primitive) integer from a collection.
     *
     * @param  <T> type of the collection's elements
     * @param  c   collection
     * @return     array with the length of {@link Collection#size()}
     */
    public static <T> int[] toIntArray(Collection<? extends Integer> c) {
        int[] array = new int[c.size()];
        int index = 0;
        for (int el : c) {
            array[index++] = el;
        }
        return array;
    }

    /**
     * Puts into a list all elements of an array.
     *
     * @param   array array
     * @return  list
     */
    public static List<Integer> toList(int[] array) {
        List<Integer> list = new ArrayList<Integer>(array.length);
        for (int el : array) {
            list.add(el);
        }
        return list;
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
            String delimiter, String delimiterReplacement) {

        StringBuilder tokenString = new StringBuilder();
        int index = 0;
        for (Object o : collection) {
            tokenString.append((index++ == 0
                                ? "" // NOI18N
                                : delimiter) +
                    o.toString().replace(delimiter, delimiterReplacement));
        }
        return tokenString.toString();
    }

    /**
     * Inserts into one collection all elements of another collection not
     * contained in that collection.
     *
     * Uses {@link Collection#contains(java.lang.Object)} to compare elements.
     *
     * @param <T>    the collection's element type
     * @param src    source collection to get elements from
     * @param target target collection to put elements into
     */
    public static <T> void addNotContainedElements(
            Collection<? extends T> src, Collection<? super T> target) {

        assert src != target : "Collections equals!"; // NOI18N
        if (src == target) return;

        for (T t : src) {
            if (!target.contains(t)) {
                target.add(t);
            }
        }
    }

    private ArrayUtil() {
    }
}
