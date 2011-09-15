package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the absolute path names of two files ascending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class FilepathIgnoreCaseAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 6513088217894871140L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getAbsolutePath().compareToIgnoreCase(rightFile.getAbsolutePath());
    }
}
