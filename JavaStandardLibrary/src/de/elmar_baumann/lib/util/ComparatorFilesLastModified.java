package de.elmar_baumann.lib.util;

import java.io.File;
import java.util.Comparator;

/**
 * Compares the last modification date of two files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/14
 */
public class ComparatorFilesLastModified implements Comparator<File> {

    private CompareOrder order;

    public ComparatorFilesLastModified(CompareOrder order) {
        this.order = order;
    }

    @Override
    public int compare(File o1, File o2) {
        long time1 = o1.lastModified();
        long time2 = o2.lastModified();
        return time1 == time2
            ? 0
            : order.equals(CompareOrder.Ascending)
            ? time1 < time2 ? -1 : 1
            : time1 > time2 ? -1 : 1;
    }
}
