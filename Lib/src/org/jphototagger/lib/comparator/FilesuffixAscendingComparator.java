package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the suffixes of two files ascending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class FilesuffixAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 332879468887099729L;

    @Override
    public int compare(File leftFile, File rightFile) {
        FileSuffixes suffixes = CompareUtil.createFileSuffixes(leftFile, rightFile, false);

        return suffixes.leftFileSuffix.compareTo(suffixes.rightFileSuffix);
    }
}
