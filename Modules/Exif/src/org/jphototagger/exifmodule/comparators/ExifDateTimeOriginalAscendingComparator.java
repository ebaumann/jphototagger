package org.jphototagger.exifmodule.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.ClassEquality;

/**
 * @author Elmar Baumann
 */
public final class ExifDateTimeOriginalAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = getTimestampDateTimeOriginalFromRepository(fileLeft);
        long timeRight = getTimestampDateTimeOriginalFromRepository(fileRight);

        return timeLeft == timeRight
                ? 0
                : timeLeft < timeRight
                ? -1
                : 1;
    }

    private long getTimestampDateTimeOriginalFromRepository(File imageFile) {
        Exif exif = repo.findExifOfImageFile(imageFile);

        if (exif == null || exif.getDateTimeOriginal() == null) {
            return imageFile.lastModified();
        }

        return exif.getDateTimeOriginal().getTime();
    }

    @Override
    public String toString() {
        return "Exif DateTimeOriginal only Date Ascending";
    }
}
