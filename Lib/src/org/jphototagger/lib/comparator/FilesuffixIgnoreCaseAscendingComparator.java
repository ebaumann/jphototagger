package org.jphototagger.lib.comparator;

import org.jphototagger.lib.util.ClassEquality;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the suffixes of two files ascending case insensitive.
 *
 * @author Elmar Baumann
 */
public final class FilesuffixIgnoreCaseAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 2364140969938240256L;

    @Override
    public int compare(File leftFile, File rightFile) {
        FileSuffixes suffixes = CompareUtil.createFileSuffixes(leftFile, rightFile, true);

        return suffixes.leftFileSuffix.compareToIgnoreCase(suffixes.rightFileSuffix);
    }
}
