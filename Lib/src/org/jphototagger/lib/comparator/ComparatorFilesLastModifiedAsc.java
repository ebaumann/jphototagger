package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the last modification time of two files ascending.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesLastModifiedAsc extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 8350747112635768758L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = fileLeft.lastModified();
        long timeRight = fileRight.lastModified();

        return (timeLeft == timeRight)
               ? 0
               : (timeLeft < timeRight)
                 ? -1
                 : 1;
    }
}
