package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Sorts file paths ascending with {@link NaturalStringSortComparator}.
 *
 * @author Elmar Baumann Applied to Files
 */
public final class FilepathNaturalSortAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(File o1, File o2) {
        return NaturalStringSortComparator.INSTANCE.compare(o1.getAbsolutePath().toLowerCase(), o2.getAbsolutePath().toLowerCase());
    }

    @Override
    public String toString() {
        return "Filepath Natural Sort Order Ascending";
    }
}
