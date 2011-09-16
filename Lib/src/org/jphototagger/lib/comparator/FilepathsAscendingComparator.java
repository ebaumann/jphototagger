package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the absolute path names of two files ascending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class FilepathsAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 8731853438844814710L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getAbsolutePath().compareTo(rightFile.getAbsolutePath());
    }
}
