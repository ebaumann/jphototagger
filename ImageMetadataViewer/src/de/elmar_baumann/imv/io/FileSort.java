package de.elmar_baumann.imv.io;

import de.elmar_baumann.lib.util.ComparatorFilesLastModified;
import de.elmar_baumann.lib.util.ComparatorFilesNames;
import de.elmar_baumann.lib.util.ComparatorFilesPostfixes;
import de.elmar_baumann.lib.util.CompareCase;
import de.elmar_baumann.lib.util.CompareOrder;
import java.io.File;
import java.util.Comparator;

/**
 * Sorting of files.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum FileSort {

    NamesAscending(new ComparatorFilesNames(CompareOrder.Ascending, CompareCase.Ignore)),
    NamesDescending(new ComparatorFilesNames(CompareOrder.Descending, CompareCase.Ignore)),
    TypesAscending(new ComparatorFilesPostfixes(CompareOrder.Ascending, CompareCase.Ignore)),
    TypesDescending(new ComparatorFilesPostfixes(CompareOrder.Descending, CompareCase.Ignore)),
    LastModifiedAscending(new ComparatorFilesLastModified(CompareOrder.Ascending)),
    LastModifiedDescending(new ComparatorFilesLastModified(CompareOrder.Descending));
    private final Comparator<File> comparator;

    private FileSort(Comparator<File> comparator) {
        this.comparator = comparator;
    }

    public Comparator<File> getComparator() {
        return comparator;
    }
}
