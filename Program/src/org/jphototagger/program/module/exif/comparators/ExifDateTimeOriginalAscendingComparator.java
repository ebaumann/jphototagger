package org.jphototagger.program.module.exif.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.lib.util.ClassEquality;

/**
 * @author Elmar Baumann
 */
public final class ExifDateTimeOriginalAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -7558718187586080760L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = ExifCompareUtil.getTimestampDateTimeOriginalFromRepository(fileLeft);
        long timeRight = ExifCompareUtil.getTimestampDateTimeOriginalFromRepository(fileRight);

        return timeLeft == timeRight
                ? 0
                : timeLeft < timeRight
                ? -1
                : 1;
    }
}
