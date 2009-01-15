package de.elmar_baumann.imv.io;

import de.elmar_baumann.lib.util.ComparatorFilesLastModified;
import de.elmar_baumann.lib.util.ComparatorFilesNames;
import de.elmar_baumann.lib.util.ComparatorFilesPostfixes;
import java.io.File;
import java.util.Comparator;

/**
 * Sorting of files.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSort {

    NamesAscending(ComparatorFilesNames.COMPARE_ASCENDING_IGNORE_CASE),
    NamesDescending(ComparatorFilesNames.COMPARE_DESCENDING_IGNORE_CASE),
    TypesAscending(ComparatorFilesPostfixes.COMPARE_ASCENDING_IGNORE_CASE),
    TypesDescending(ComparatorFilesPostfixes.COMPARE_DESCENDING_IGNORE_CASE),
    LastModifiedAscending(ComparatorFilesLastModified.COMPARE_ASCENDING),
    LastModifiedDescending(ComparatorFilesLastModified.COMPARE_DESCENDING);
    private final Comparator<File> comparator;

    private FileSort(Comparator<File> comparator) {
        this.comparator = comparator;
    }

    public Comparator<File> getComparator() {
        return comparator;
    }
}
