package org.jphototagger.lib.comparator;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.util.Comparator;

/**
 * Compares the suffixes of two files descending case sensitive.
 *
 * @author Elmar Baumann
 */
public final class ComparatorFilesSuffixesDescCs extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 8024613319134189366L;

    @Override
    public int compare(File leftFile, File rightFile) {
        Pair<String, String> suffixes = Util.getCmpSuffixes(leftFile, rightFile, false);

        return suffixes.getFirst().compareTo(suffixes.getSecond()) * -1;
    }
}
