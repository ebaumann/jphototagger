package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the suffixes of two files descending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class FilesuffixIgnoreCaseDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ReverseComparator<File> delegate = new ReverseComparator<File>(new FilesuffixIgnoreCaseAscendingComparator());

    @Override
    public int compare(File leftFile, File rightFile) {
        return delegate.compare(leftFile, rightFile);
    }

    @Override
    public String toString() {
        return "File Suffix ignoring case Descending";
    }
}
