package org.jphototagger.program.comparator;

/**
 * Compares numbers that can be null. If a number is null, it is always lesser
 * than a number that is not null.
 *
 * @author Elmar Baumann
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
        return ((l1 == null) && (l2 == null))
               ? 0
               : ((l1 == null) && (l2 != null))
                 ? -1
                 : ((l1 != null) && (l2 == null))
                   ? 1
                   : (l1.longValue() == l2.longValue())
                     ? 0
                     : (l1.longValue() > l2.longValue())
                       ? 1
                       : -1;
    }

    private NumberCompare() {}
}
