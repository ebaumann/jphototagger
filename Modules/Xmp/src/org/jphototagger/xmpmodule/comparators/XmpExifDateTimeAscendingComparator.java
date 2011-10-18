package org.jphototagger.xmpmodule.comparators;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.openide.util.Lookup;

import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.ClassEquality;

/**
 * @author Elmar Baumann
 */
public final class XmpExifDateTimeAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private static final SimpleDateFormat EXIF_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int compare(File fileLeft, File fileRight) {
        String timeLeft = getTimeString(fileLeft);
        String timeRight = getTimeString(fileRight);

        return timeLeft.compareTo(timeRight);
    }

    private String getTimeString(File file) {
        long exifTimestamp = repo.findExifDateTimeOriginalTimestamp(file);
        boolean hasExif = exifTimestamp >= 0;

        if (hasExif) {
            Date exifDate = new Date(exifTimestamp);
            return EXIF_DATE_FORMAT.format(exifDate);
        }

        String xmpDate = repo.findXmpIptc4CoreDateCreated(file);

        return xmpDate != null ? xmpDate : "";
    }

    @Override
    public String toString() {
        return "XMP and EXIF Dates Ascending";
    }
}
