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

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utils for arrays and array like objects.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class ArrayUtil {

    /**
     * Converts an array of objects into an array of strings. The strings are
     * created with the object's <code>toString()</code> method.
     *
     * @param  objectArray array of objects
     * @return             array of strings
     * @throws             IllegalArgumentException if an element in the array
     *                                              of objects is null
     */
    public static String[] toStringArray(Object[] objectArray) {
        if (objectArray == null) throw new NullPointerException("array == null");

        String[] stringArray = new String[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            if (objectArray[i] == null) throw new IllegalArgumentException("Element at index " + i + " is null");

            Object object = objectArray[i];
            stringArray[i] = object.toString();
        }
        return stringArray;
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
        int   index = 0;
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

    public static boolean byteArraysEquals(byte[] left, byte[] right) {
        if (left == null)  throw new NullPointerException("Left byte array is null!");
        if (right == null) throw new NullPointerException("Right byte array is null!");

        if (left.length != right.length) return false;

        for (int i = 0; i < left.length; i++) {
            if (left[i] != right[i]) return false;
        }
        return true;
    }

    private ArrayUtil() {
    }
}
