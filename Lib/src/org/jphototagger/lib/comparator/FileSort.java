package org.jphototagger.lib.comparator;

import java.io.File;

import java.util.Comparator;

/**
 * Sorting of files.
 *
 * @author Elmar Baumann
 */
public enum FileSort {
    PATHS_ASCENDING(new ComparatorFilesPathsAscCi()), PATHS_DESCENDING(new ComparatorFilesPathsDescCi()),
    NAMES_ASCENDING(new ComparatorFilesNamesAscCi()), NAMES_DESCENDING(new ComparatorFilesNamesDescCi()),
    TYPES_ASCENDING(new ComparatorFilesSuffixesAscCi()), TYPES_DESCENDING(new ComparatorFilesSuffixesDescCi()),
    LAST_MODIFIED_ASCENDING(new ComparatorFilesLastModifiedAsc()),
    LAST_MODIFIED_DESCENDING(new ComparatorFilesLastModifiedDesc()), NO_SORT(new ComparatorFilesNoSort()),
    ;

    private final Comparator<File> comparator;

    private FileSort(Comparator<File> comparator) {
        this.comparator = comparator;
    }

    public Comparator<File> getComparator() {
        return comparator;
    }
}
