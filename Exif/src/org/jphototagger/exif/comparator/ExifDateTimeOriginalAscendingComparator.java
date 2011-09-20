package org.jphototagger.exif.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.jphototagger.exif.ExifMetadata;
import org.jphototagger.lib.util.ClassEquality;

/**
 * Compares ascending the date time original of two image files based on their
 * EXIF metadata. If an image file has no EXIF metadata it's last modification
 * file time will be used.
 *
 * @author Elmar Baumann
 */
public final class ExifDateTimeOriginalAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -7558718187586080760L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = ExifMetadata.timestampDateTimeOriginalDb(fileLeft);
        long timeRight = ExifMetadata.timestampDateTimeOriginalDb(fileRight);

        return (timeLeft == timeRight)
                ? 0
                : (timeLeft < timeRight)
                ? -1
                : 1;
    }
}
