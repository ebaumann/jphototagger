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
public final class ComparatorFilesSuffixesDescCi extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 10242248897095575L;

    @Override
    public int compare(File leftFile, File rightFile) {
        CmpFileSuffixes suffixes = Util.getCmpSuffixes(leftFile, rightFile, true);

        return suffixes.leftFileSuffix.compareToIgnoreCase(suffixes.rightFileSuffix) * -1;
    }
}
