package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 * Compares the absolute path names of two files ascending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesPathsAscCs extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 8731853438844814710L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getAbsolutePath().compareTo(
            rightFile.getAbsolutePath());
    }
}
