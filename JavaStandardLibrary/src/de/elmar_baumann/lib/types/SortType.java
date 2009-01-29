package de.elmar_baumann.lib.types;

/**
 * Defines in which order objects can be sorted.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum SortType {

    /** Objects are not sorted */
    none, 
    /** Objects sorted ascending, case sensitve */
    ascending,
    /** Objects sorted descending, case sensitve*/
    descending,
    /** Objects sorted ascending, case <em>in</em>sensitive */
    ascendingNoCase,
    /** Objects sorted descending, case <em>in</em>sensitive */
    descendingNoCase,
}
