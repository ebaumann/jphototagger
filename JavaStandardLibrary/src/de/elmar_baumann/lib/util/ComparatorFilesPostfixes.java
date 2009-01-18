package de.elmar_baumann.lib.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the postfixes of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public final class ComparatorFilesPostfixes implements Comparator<File> {

    /**
     * Compares the postfixes of two files case insensitive to sort them in
     * ascending order.
     *
     * The postfix is the string after the last period of the filename.
     */
    public final static ComparatorFilesPostfixes COMPARE_ASCENDING_IGNORE_CASE =
        new ComparatorFilesPostfixes(CompareOrder.Ascending, CompareCase.Ignore);
    /**
     * Compares the postfixes of two files case sensitive to sort them in
     * ascending order.
     *
     * The postfix is the string after the last period of the filename.
     */
    public final static ComparatorFilesPostfixes COMPARE_ASCENDING_CASE_SENSITIVE =
        new ComparatorFilesPostfixes(CompareOrder.Ascending, CompareCase.Sensitive);
    /**
     * Compares the postfixes of two files case insensitive to sort them in
     * descending order.
     *
     * The postfix is the string after the last period of the filename.
     */
    public final static ComparatorFilesPostfixes COMPARE_DESCENDING_IGNORE_CASE =
        new ComparatorFilesPostfixes(CompareOrder.Descending, CompareCase.Ignore);
    /**
     * Compares the postfixes of two files case sensitive to sort them in
     * descending order.
     *
     * The postfix is the string after the last period of the filename.
     */
    public final static ComparatorFilesPostfixes COMPARE_DESCENDING_CASE_SENSITIVE =
        new ComparatorFilesPostfixes(CompareOrder.Descending, CompareCase.Sensitive);
    /** Sort order */
    private final CompareOrder compareOrder;
    /** Ignore case? */
    private final CompareCase compareCase;

    private ComparatorFilesPostfixes(CompareOrder compareOrder, CompareCase compareCase) {
        this.compareOrder = compareOrder;
        this.compareCase = compareCase;
    }

    @Override
    public int compare(File leftFile, File rightFile) {
        String compare1 = leftFile.getName();
        String compare2 = rightFile.getName();
        int index1 = compare1.lastIndexOf(".");
        int index2 = compare2.lastIndexOf(".");
        compare1 = index1 >= 0 && index1 < compare1.length() - 1 ? compare1.substring(index1 + 1) : "";
        compare2 = index2 >= 0 && index2 < compare2.length() - 1 ? compare2.substring(index2 + 1) : "";
        boolean postfixesEquals = compare1.isEmpty() || compare1.isEmpty() ||
            compareCase.equals(CompareCase.Ignore)
            ? compare1.equalsIgnoreCase(compare2)
            : compare1.equals(compare2);
        if (postfixesEquals) {
            compare1 = leftFile.getAbsolutePath();
            compare2 = rightFile.getAbsolutePath();
        }
        return compareOrder.equals(CompareOrder.Ascending)
            ? compareCase.equals(CompareCase.Ignore)
            ? compare1.compareToIgnoreCase(compare2)
            : compare1.compareTo(compare2)
            : compareCase.equals(CompareCase.Ignore)
            ? compare2.compareToIgnoreCase(compare1)
            : compare2.compareTo(compare1);
    }
}
