package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the size of two files ascending.
 *
 * @author Elmar Baumann
 */
public final class FileSizeAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        long lengthLeft = fileLeft.length();
        long lengthRight = fileRight.length();

        return (lengthLeft == lengthRight)
                ? 0
                : (lengthLeft < lengthRight)
                ? -1
                : 1;
    }

    @Override
    public String toString() {
        return "File Size Ascending";
    }
}
