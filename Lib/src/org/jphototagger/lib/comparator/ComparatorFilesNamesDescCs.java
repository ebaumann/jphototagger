package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the file names of two files descending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesNamesDescCs extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = -2223682373452148530L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareTo(rightFile.getName()) * -1;
    }
}
