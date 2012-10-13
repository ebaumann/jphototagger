package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Does not change the sort order.
 *
 * @author Elmar Baumann
 */
public final class FileUnsortedComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Returns always zero.
     *
     * @param  leftFile  1. file to compare
     * @param  rightFile 2. file to compare
     * @return           zero
     */
    @Override
    public int compare(File leftFile, File rightFile) {
        return 0;
    }

    @Override
    public String toString() {
        return "File Unsorted";
    }
}
