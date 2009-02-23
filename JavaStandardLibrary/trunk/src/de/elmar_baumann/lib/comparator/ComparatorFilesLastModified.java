package de.elmar_baumann.lib.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the last modification time of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public final class ComparatorFilesLastModified implements Comparator<File> {

    /**
     * Compares two file modification times to sort files in ascending order:
     * If in <code>compare()</code> the left file is newer than the right
     * file, the return value is positive. If in <code>compare()</code>
     * the left file is older than the right file, the return value is
     * negative. If the modification time of both files are equal,
     * <code>compare()</code> returns zero.
     */
    public static final ComparatorFilesLastModified COMPARE_ASCENDING =
        new ComparatorFilesLastModified(CompareOrder.ASCENDING);
    /**
     * Compares two file modification times to sort files in descending order:
     * If in <code>compare()</code> the left file is newer than the right
     * file, the return value is negative. If in <code>compare()</code>
     * the left file is older than the right file, the return value is
     * positive. If the modification time of both files are equal,
     * <code>compare()</code> returns zero.
     */
    public static final ComparatorFilesLastModified COMPARE_DESCENDING =
        new ComparatorFilesLastModified(CompareOrder.DESCENDING);
    /** Sort order of the files */
    private final CompareOrder order;

    private ComparatorFilesLastModified(CompareOrder order) {
        this.order = order;
    }

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = fileLeft.lastModified();
        long timeRight = fileRight.lastModified();
        return timeLeft == timeRight
            ? 0
            : order.equals(CompareOrder.ASCENDING)
            ? timeLeft < timeRight ? -1 : 1
            : timeLeft > timeRight ? -1 : 1;
    }
}
