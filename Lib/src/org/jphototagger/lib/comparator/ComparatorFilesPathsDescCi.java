package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 * Compares the absolute path names of two files descending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesPathsDescCi extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 5524247857708597709L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getAbsolutePath().compareToIgnoreCase(rightFile.getAbsolutePath()) * -1;
    }
}
