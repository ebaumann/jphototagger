package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.program.image.metadata.exif.ExifMetadata;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares descending the date time original of two image files based on their
 * EXIF metadata. If an image file has no EXIF metadata it's last modification
 * file time will be used.
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifDateTimeOriginalDesc extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = -288734067911706453L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = ExifMetadata.timestampDateTimeOriginalDb(fileLeft);
        long timeRight = ExifMetadata.timestampDateTimeOriginalDb(fileRight);

        return (timeLeft == timeRight)
               ? 0
               : (timeLeft > timeRight)
                 ? -1
                 : 1;
    }
}
