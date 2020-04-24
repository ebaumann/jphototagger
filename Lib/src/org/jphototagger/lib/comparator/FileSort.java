package org.jphototagger.lib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * @author Elmar Baumann
 */
public enum FileSort {

    PATHS_ASCENDING(new FilepathIgnoreCaseAscendingComparator()),
    PATHS_DESCENDING(new FilepathIgnoreCaseDescendingComparator()),
    NAMES_ASCENDING(new FilenameIgnoreCaseAscendingComparator()),
    NAMES_DESCENDING(new FilenameIgnoreCaseDescendingComparator()),
    PATHS_NATURAL_ORDER_ASCENDING(new FilepathNaturalSortAscendingComparator()),
    PATHS_NATURAL_ORDER_DESCENDING(new FilepathNaturalSortDescendingComparator()),
    NAMES_NATURAL_ORDER_ASCENDING(new FilenameNaturalSortAscendingComparator()),
    NAMES_NATURAL_ORDER_DESCENDING(new FilenameNaturalSortDescendingComparator()),
    TYPES_ASCENDING(new FilesuffixIgnoreCaseAscendingComparator()),
    TYPES_DESCENDING(new FilesuffixIgnoreCaseDescendingComparator()),
    LAST_MODIFIED_ASCENDING(new FileLastModifiedAscendingComparator()),
    LAST_MODIFIED_DESCENDING(new FileLastModifiedDescendingComparator()),
    SIZE_ASCENDING(new FileSizeAscendingComparator()),
    SIZE_DESCENDING(new FileSizeDescendingComparator()),
    NO_SORT(new FileUnsortedComparator()),;
    private final Comparator<File> comparator;

    private FileSort(Comparator<File> comparator) {
        this.comparator = comparator;
    }

    public Comparator<File> getComparator() {
        return comparator;
    }
}
