package org.jphototagger.exifmodule.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.ClassEquality;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifTimestampOriginalAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = repo.findExifDateTimeOriginalTimestamp(fileLeft);
        long timeRight = repo.findExifDateTimeOriginalTimestamp(fileRight);

        return timeLeft == timeRight
                ? 0
                : timeLeft < timeRight
                ? -1
                : 1;
    }

    @Override
    public String toString() {
        return "EXIF DateTimeOriginal Date and Time Ascending";
    }
}
