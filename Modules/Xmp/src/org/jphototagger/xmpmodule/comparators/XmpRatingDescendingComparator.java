package org.jphototagger.xmpmodule.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.lib.comparator.ReverseComparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * @author Elmar Baumann
 */
public final class XmpRatingDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ReverseComparator<File> delegate = new ReverseComparator<>(new XmpRatingAscendingComparator());

    @Override
    public int compare(File fileLeft, File fileRight) {
        return delegate.compare(fileLeft, fileRight);
    }

    @Override
    public String toString() {
        return "XMP Rating Descending";
    }
}
