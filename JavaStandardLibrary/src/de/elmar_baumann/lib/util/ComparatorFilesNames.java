package de.elmar_baumann.lib.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the absolute path names of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public final class ComparatorFilesNames implements Comparator<File> {

    /**
     * Compares the names of two files case insensitive to sort them in
     * ascending order.
     */
    public static final ComparatorFilesNames COMPARE_ASCENDING_IGNORE_CASE =
        new ComparatorFilesNames(CompareOrder.Ascending, CompareCase.Ignore);
    /**
     * Compares the names of two files case sensitive to sort them in
     * ascending order.
     */
    public static final ComparatorFilesNames COMPARE_ASCENDING_CASE_SENSITIVE =
        new ComparatorFilesNames(CompareOrder.Ascending, CompareCase.Sensitive);
    /**
     * Compares the names of two files case insensitive to sort them in
     * descending order.
     */
    public static final ComparatorFilesNames COMPARE_DESCENDING_IGNORE_CASE =
        new ComparatorFilesNames(CompareOrder.Descending, CompareCase.Ignore);
    /**
     * Compares the names of two files case sensitive to sort them in
     * descending order.
     */
    public static final ComparatorFilesNames COMPARE_DESCENDING_CASE_SENSITIVE =
        new ComparatorFilesNames(CompareOrder.Descending, CompareCase.Sensitive);
    /** Sort order of the files */
    private final CompareOrder compareOrder;
    /** Case sensitive? */
    private final CompareCase compareCase;

    private ComparatorFilesNames(CompareOrder compareOrder, CompareCase compareCase) {
        this.compareOrder = compareOrder;
        this.compareCase = compareCase;
    }

    /**
     * Compares the absolute path names of of two files. Uses {@link CompareOrder}
     * and {@link CompareCase}.
     *
     * @param leftFile   left file
     * @param rightFile  right file
     * @return zero if both file paths are equals, less than zero if the
     *         left file's sort order is before the right file's sort order
     *         and greater than zero if the right file's sort order is before
     *         the left file's sort order
     */
    @Override
    public int compare(File leftFile, File rightFile) {
        String leftFilename =
            compareCase.equals(CompareCase.Ignore)
            ? leftFile.getAbsolutePath().toLowerCase()
            : leftFile.getAbsolutePath();
        String rightFilename =
            compareCase.equals(CompareCase.Ignore)
            ? rightFile.getAbsolutePath().toLowerCase()
            : rightFile.getAbsolutePath();
        int resultAscending = leftFilename.compareTo(rightFilename);
        return compareOrder.equals(CompareOrder.Ascending)
            ? resultAscending : resultAscending * -1;
    }
}
