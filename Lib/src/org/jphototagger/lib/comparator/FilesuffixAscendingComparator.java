package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares the suffixes of two files ascending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class FilesuffixAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(File leftFile, File rightFile) {
        String leftSuffix = FileUtil.getSuffix(leftFile);
        String rightSuffix = FileUtil.getSuffix(rightFile);

        return leftSuffix.compareTo(rightSuffix);
    }

    @Override
    public String toString() {
        return "File Suffix Ascending";
    }
}
