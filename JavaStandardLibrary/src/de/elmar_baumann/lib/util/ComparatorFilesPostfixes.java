package de.elmar_baumann.lib.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the postfixes of two files
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public class ComparatorFilesPostfixes implements Comparator<File> {

    private CompareOrder order;
    private CompareCase ccase;

    public ComparatorFilesPostfixes(CompareOrder order, CompareCase ccase) {
        this.order = order;
        this.ccase = ccase;
    }

    @Override
    public int compare(File o1, File o2) {
        String compare1 = o1.getName();
        String compare2 = o2.getName();
        int index1 = compare1.lastIndexOf(".");
        int index2 = compare2.lastIndexOf(".");
        compare1 = index1 >= 0 && index1 < compare1.length() - 1 ? compare1.substring(index1 + 1) : "";
        compare2 = index2 >= 0 && index2 < compare2.length() - 1 ? compare2.substring(index2 + 1) : "";
        boolean postfixesEquals = compare1.isEmpty() || compare1.isEmpty() ||
            ccase.equals(CompareCase.Ignore)
            ? compare1.equalsIgnoreCase(compare2)
            : compare1.equals(compare2);
        if (postfixesEquals) {
            compare1 = o1.getAbsolutePath();
            compare2 = o2.getAbsolutePath();
        }
        return order.equals(CompareOrder.Ascending)
            ? ccase.equals(CompareCase.Ignore)
            ? compare1.compareToIgnoreCase(compare2)
            : compare1.compareTo(compare2)
            : ccase.equals(CompareCase.Ignore)
            ? compare2.compareToIgnoreCase(compare1)
            : compare2.compareTo(compare1);
    }
}
