package de.elmar_baumann.lib.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the suffixes of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public final class ComparatorFilesSuffixes implements Comparator<File> {

    /**
     * Compares the suffixes of two files case insensitive to sort them in
     * ascending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes COMPARE_ASCENDING_IGNORE_CASE =
        new ComparatorFilesSuffixes(CompareOrder.ASCENDING, CompareCase.IGNORE);
    /**
     * Compares the suffixes of two files case sensitive to sort them in
     * ascending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes COMPARE_ASCENDING_CASE_SENSITIVE =
        new ComparatorFilesSuffixes(CompareOrder.ASCENDING, CompareCase.SENSITIVE);
    /**
     * Compares the suffixes of two files case insensitive to sort them in
     * descending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes COMPARE_DESCENDING_IGNORE_CASE =
        new ComparatorFilesSuffixes(CompareOrder.DESCENDING, CompareCase.IGNORE);
    /**
     * Compares the suffixes of two files case sensitive to sort them in
     * descending order.
     *
     * The suffix is the string after the last period of the filename.
     */
    public final static ComparatorFilesSuffixes COMPARE_DESCENDING_CASE_SENSITIVE =
        new ComparatorFilesSuffixes(CompareOrder.DESCENDING, CompareCase.SENSITIVE);
    /** Sort order */
    private final CompareOrder compareOrder;
    /** IGNORE case? */
    private final CompareCase compareCase;

    private ComparatorFilesSuffixes(CompareOrder compareOrder, CompareCase compareCase) {
        this.compareOrder = compareOrder;
        this.compareCase = compareCase;
    }

    @Override
    public int compare(File leftFile, File rightFile) {
        String leftSuffix = leftFile.getName();
        String rightSuffix = rightFile.getName();
        int indexLeftPeriod = leftSuffix.lastIndexOf(".");
        int indexRightPeriod = rightSuffix.lastIndexOf(".");

        leftSuffix = indexLeftPeriod >= 0 && indexLeftPeriod < leftSuffix.length() - 1
            ? leftSuffix.substring(indexLeftPeriod + 1) : "";
        rightSuffix = indexRightPeriod >= 0 && indexRightPeriod < rightSuffix.length() - 1
            ? rightSuffix.substring(indexRightPeriod + 1) : "";

        boolean suffixesEquals = leftSuffix.isEmpty() || leftSuffix.isEmpty() ||
            compareCase.equals(CompareCase.IGNORE)
            ? leftSuffix.equalsIgnoreCase(rightSuffix)
            : leftSuffix.equals(rightSuffix);

        if (suffixesEquals) {
            leftSuffix = leftFile.getAbsolutePath();
            rightSuffix = rightFile.getAbsolutePath();
        }

        return compareOrder.equals(CompareOrder.ASCENDING)
            ? compareCase.equals(CompareCase.IGNORE)
            ? leftSuffix.compareToIgnoreCase(rightSuffix)
            : leftSuffix.compareTo(rightSuffix)
            : compareCase.equals(CompareCase.IGNORE)
            ? rightSuffix.compareToIgnoreCase(leftSuffix)
            : rightSuffix.compareTo(leftSuffix);
    }
}
