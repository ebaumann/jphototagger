package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the absolute path names of two files descending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class FilepathIgnoreCaseDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 5524247857708597709L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getAbsolutePath().compareToIgnoreCase(rightFile.getAbsolutePath()) * -1;
    }
}
