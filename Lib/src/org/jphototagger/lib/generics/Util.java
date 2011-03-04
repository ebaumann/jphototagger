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
    public static <A, B> List<? extends A> firstOfPairs(Collection<Pair<? extends A, ? extends B>> pairs) {
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
    public static <A, B> List<? extends B> secondOfPairs(Collection<Pair<? extends A, ? extends B>> pairs) {
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
