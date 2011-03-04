package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 * Compares the file names of two files ascending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesNamesAscCi extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 499069627201609023L;

    @Override
    public int compare(File leftFile, File rightFile) {
        return leftFile.getName().compareToIgnoreCase(rightFile.getName());
    }
}
