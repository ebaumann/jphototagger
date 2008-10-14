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
        String postfix1 = o1.getName();
        String postfix2 = o2.getName();
        int index1 = postfix1.lastIndexOf(".");
        int index2 = postfix2.lastIndexOf(".");
        postfix1 = index1 < postfix1.length() - 1 ? postfix1.substring(index1 + 1) : "";
        postfix2 = index2 < postfix2.length() - 1 ? postfix2.substring(index1 + 1) : "";
        return order.equals(CompareOrder.Ascending)
            ? ccase.equals(CompareCase.Ignore)
            ? postfix1.compareToIgnoreCase(postfix2)
            : postfix1.compareTo(postfix2)
            : ccase.equals(CompareCase.Ignore)
            ? postfix2.compareToIgnoreCase(postfix1)
            : postfix2.compareTo(postfix1);
    }
}
