package de.elmar_baumann.imv.comparator;

import java.util.Comparator;

/**
 * Compares strings in ascending order.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/05
 */
public final class ComparatorStringAscending implements Comparator<String> {

    public static final ComparatorStringAscending CASE_SENSITIVE =
            new ComparatorStringAscending(false);
    public static final ComparatorStringAscending IGNORE_CASE =
            new ComparatorStringAscending(true);
    private final boolean ignoreCase;

    private ComparatorStringAscending(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    @Override
    public int compare(String o1, String o2) {
        return ignoreCase
               ? o1.compareToIgnoreCase(o2)
               : o1.compareTo(o2);
    }
}
