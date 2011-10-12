package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the file names of two files ascending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class FilenameAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareTo(rightFile.getName());
    }
}
