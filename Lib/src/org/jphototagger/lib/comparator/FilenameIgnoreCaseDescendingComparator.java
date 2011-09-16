package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the file names of two files descending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class FilenameIgnoreCaseDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -1878605986535841710L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareToIgnoreCase(rightFile.getName()) * -1;
    }
}
