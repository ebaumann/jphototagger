package org.jphototagger.program.module.imagecollections;

import java.text.Collator;
import java.util.Comparator;

/**
 * Compares strings in ascending order.
 *
 * @author Elmar Baumann
 */
public final class StringAscendingComparator implements Comparator<String> {

    public static final StringAscendingComparator INSTANCE = new StringAscendingComparator();
    private final Collator collator = Collator.getInstance();

    @Override
    public int compare(String s1, String s2) {
        return ((s1 == null) && (s2 == null))
                ? 0
                : ((s1 == null) && (s2 != null))
                ? -1
                : ((s1 != null) && (s2 == null))
                ? 1
                : collator.compare(s1, s2);
    }
}
