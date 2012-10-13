package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the last modification time of two files ascending.
 *
 * @author Elmar Baumann
 */
public final class FileLastModifiedAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

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

    @Override
    public String toString() {
        return "File Last Modified Ascending";
    }
}
