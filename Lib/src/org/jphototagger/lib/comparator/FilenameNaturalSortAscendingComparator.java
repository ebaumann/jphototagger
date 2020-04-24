package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Sorts file names (only names, not parent directories i.e. file paths)
 * ascending with {@link NaturalStringSortComparator}.
 *
 * @author Elmar Baumann Applied to Files
 */
public final class FilenameNaturalSortAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(File o1, File o2) {
        return NaturalStringSortComparator.INSTANCE.compare(o1.getName().toLowerCase(), o2.getName().toLowerCase());
    }

    @Override
    public String toString() {
        return "Filename Natural Sort Order Ascending";
    }
}
