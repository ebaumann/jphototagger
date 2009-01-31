package de.elmar_baumann.lib.types;

/**
 * Defines in which order objects can be sorted.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum SortType {

    /** Objects are not sorted */
    NONE,
    /** Objects sorted ASCENDING, case sensitve */
    ASCENDING,
    /** Objects sorted DESCENDING, case sensitve*/
    DESCENDING,
    /** Objects sorted ASCENDING, case <em>in</em>sensitive */
    ASCENDING_NO_CASE,
    /** Objects sorted DESCENDING, case <em>in</em>sensitive */
    DESCENDING_NO_CASE,
}
