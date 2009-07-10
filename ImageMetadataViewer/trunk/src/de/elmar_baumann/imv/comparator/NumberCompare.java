package de.elmar_baumann.imv.comparator;

/**
 * Compares numbers that can be null. If a number is null, it is always lesser
 * than a number that is not null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
final class NumberCompare {

    /**
     * Compares two {@link java.lang.Long} values.
     * 
     * @param l1 long value 1
     * @param l2 long value 2
     * @return   A negative integer when l1 is less than l2, zero if both are
     *           equals and a positive integer when l1 is greater than l2.
     */
    static int compare(Long l1, Long l2) {
        return l1 == l2
               ? 1
               : l1 == null // l2 can't be null if this is true (no 2nd null-query)
                 ? -1
                 : l1 > l2
                   ? 1
                   : -1;
    }

    private NumberCompare() {
    }
}
