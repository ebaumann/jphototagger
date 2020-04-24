package org.jphototagger.lib.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Sorts in the opposite direction as
 * {@link FilenameNaturalSortAscendingComparator}.
 *
 * @author Elmar Baumann
 */
public final class FilepathNaturalSortDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ReverseComparator<File> delegate = new ReverseComparator<>(new FilepathAscendingComparator());

    @Override
    public int compare(File leftFile, File rightFile) {
        return delegate.compare(leftFile, rightFile);
    }

    @Override
    public String toString() {
        return "Filepath Natural Sort Order Descending";
    }
}
