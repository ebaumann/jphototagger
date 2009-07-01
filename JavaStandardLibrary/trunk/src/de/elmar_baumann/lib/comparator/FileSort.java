package de.elmar_baumann.lib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Sorting of files.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSort {

    NAMES_ASCENDING(ComparatorFilesNames.ASCENDING_IGNORE_CASE),
    NAMES_DESCENDING(ComparatorFilesNames.DESCENDING_IGNORE_CASE),
    TYPES_ASCENDING(ComparatorFilesSuffixes.ASCENDING_IGNORE_CASE),
    TYPES_DESCENDING(ComparatorFilesSuffixes.DESCENDING_IGNORE_CASE),
    LAST_MODIFIED_ASCENDING(ComparatorFilesLastModified.ASCENDING),
    LAST_MODIFIED_DESCENDING(ComparatorFilesLastModified.DESCENDING);
    private final Comparator<File> comparator;

    private FileSort(Comparator<File> comparator) {
        this.comparator = comparator;
    }

    public Comparator<File> getComparator() {
        return comparator;
    }
}
