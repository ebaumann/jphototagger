package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Does not change the sort order.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesNoSort extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 6632501783143748216L;

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
}
