package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the absolute path names of two files descending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class FilepathDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -6952867012863772402L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getAbsolutePath().compareTo(rightFile.getAbsolutePath()) * -1;
    }
}
