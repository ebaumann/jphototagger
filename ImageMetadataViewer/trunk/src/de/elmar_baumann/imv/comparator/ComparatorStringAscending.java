package de.elmar_baumann.imv.comparator;

import java.util.Comparator;

/**
 * Compares strings in ascending order.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/05
 */
public final class ComparatorStringAscending implements Comparator<String> {

    private final boolean ignoreCase;

    public ComparatorStringAscending(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    @Override
    public int compare(String o1, String o2) {
        return ignoreCase
            ? o1.compareToIgnoreCase(o2)
            : o1.compareTo(o2);
    }
}
