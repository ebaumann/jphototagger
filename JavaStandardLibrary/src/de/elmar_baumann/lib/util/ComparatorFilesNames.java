package de.elmar_baumann.lib.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the names of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public final class ComparatorFilesNames implements Comparator<File> {

    private final CompareOrder order;
    private final CompareCase ccase;

    public ComparatorFilesNames(CompareOrder order, CompareCase ccase) {
        this.order = order;
        this.ccase = ccase;
    }

    @Override
    public int compare(File o1, File o2) {
        String filename1 = ccase.equals(CompareCase.Ignore) ? o1.getAbsolutePath().toLowerCase() : o1.getAbsolutePath();
        String filename2 = ccase.equals(CompareCase.Ignore) ? o2.getAbsolutePath().toLowerCase() : o2.getAbsolutePath();
        int asc = filename1.compareTo(filename2);
        return order.equals(CompareOrder.Ascending) ? asc : asc * -1;
    }
}
