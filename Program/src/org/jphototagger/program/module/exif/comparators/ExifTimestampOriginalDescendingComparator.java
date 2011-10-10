package org.jphototagger.program.module.exif.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.lib.util.ClassEquality;

/**
 * @author Elmar Baumann
 */
public final class ExifTimestampOriginalDescendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -7558718187586080760L;
    private final ExifInfo exifInfo = Lookup.getDefault().lookup(ExifInfo.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = exifInfo.getTimeTakenInMillis(fileLeft);
        long timeRight = exifInfo.getTimeTakenInMillis(fileRight);

        return timeLeft == timeRight
                ? 0
                : timeLeft < timeRight
                ? 1
                : -1;
    }
}
