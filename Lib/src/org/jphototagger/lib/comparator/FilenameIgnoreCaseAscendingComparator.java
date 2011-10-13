package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the file names of two files ascending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class FilenameIgnoreCaseAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareToIgnoreCase(rightFile.getName());
    }

    @Override
    public String toString() {
        return "Filename ignoring case Ascending";
    }
}
