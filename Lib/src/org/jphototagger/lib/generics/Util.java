/*
 * @(#)Util.java    Created on 2008-09-18
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

package org.jphototagger.lib.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utils for generics.
 *
 * @author Elmar Baumann
 */
public final class Util {

    /**
     * Returns the first elements of a collection of pairs.
     *
     * @param <A>   type of first elements
     * @param <B>   type of second elements
     * @param pairs pairs
     * @return      all first elements of that pairs in iterator order of that
     *              collection
     * @throws      NullPointerException if pairs is null
     */
    public static <A, B> List<? extends A> firstOfPairs(
            Collection<Pair<? extends A, ? extends B>> pairs) {
        if (pairs == null) {
            throw new NullPointerException("pairs == null");
        }

        List<A> list = new ArrayList<A>(pairs.size());

        for (Pair<? extends A, ? extends B> pair : pairs) {
            list.add(pair.getFirst());
        }

        return list;
    }

    /**
     * Returns the second elements of a collection of pairs.
     *
     * @param <A>   type of first elements
     * @param <B>   type of second elements
     * @param pairs pairs
     * @return      all second elements of that pairs in iterator order of that
     *              collection
     * @throws      NullPointerException if pairs is null
     */
    public static <A, B> List<? extends B> secondOfPairs(
            Collection<Pair<? extends A, ? extends B>> pairs) {
        if (pairs == null) {
            throw new NullPointerException("pairs == null");
        }

        List<B> list = new ArrayList<B>(pairs.size());

        for (Pair<? extends A, ? extends B> pair : pairs) {
            list.add(pair.getSecond());
        }

        return list;
    }

    private Util() {}
}
