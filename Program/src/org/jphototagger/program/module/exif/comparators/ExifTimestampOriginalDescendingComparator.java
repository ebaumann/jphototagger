package org.jphototagger.program.module.exif.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.comparator.ReverseComparator;
import org.jphototagger.lib.util.ClassEquality;

/**
 * @author Elmar Baumann
 */
// Separate class: Will be instanciated via Reflection
public final class ExifTimestampOriginalDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final Comparator<File> delegate = new ReverseComparator<File>(new ExifTimestampOriginalAscendingComparator());

    @Override
    public int compare(File fileLeft, File fileRight) {
        return delegate.compare(fileLeft, fileRight);
    }

    @Override
    public String toString() {
        return "EXIF DateTimeOriginal Date and Time Descending";
    }
}
