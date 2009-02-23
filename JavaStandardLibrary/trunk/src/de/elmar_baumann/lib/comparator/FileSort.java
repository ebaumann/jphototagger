package de.elmar_baumann.lib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Sorting of files.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSort {

    NAMES_ASCENDING(ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE),
    NAMES_DESCENDING(ComparatorFilesNames.COMPARE_DESCENDING_IGNORE_CASE),
    TYPES_ASCENDING(ComparatorFilesSuffixes.COMPARE_ASCENDING_IGNORE_CASE),
    TYPES_DESCENDING(ComparatorFilesSuffixes.COMPARE_DESCENDING_IGNORE_CASE),
    LAST_MODIFIED_ASCENDING(ComparatorFilesLastModified.COMPARE_ASCENDING),
    LAST_MODIFIED_DESCENDING(ComparatorFilesLastModified.COMPARE_DESCENDING);
    private final Comparator<File> comparator;

    private FileSort(Comparator<File> comparator) {
        this.comparator = comparator;
    }

    public Comparator<File> getComparator() {
        return comparator;
    }
}
