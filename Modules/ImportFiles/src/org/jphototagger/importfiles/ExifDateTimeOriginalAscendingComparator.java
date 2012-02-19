package org.jphototagger.importfiles;

import java.io.File;
import java.util.Comparator;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.ExifInfo;

/**
 * @author Elmar Baumann
 */
public final class ExifDateTimeOriginalAscendingComparator implements Comparator<File> {

    private final ExifInfo exifInfo = Lookup.getDefault().lookup(ExifInfo.class);
    public static final ExifDateTimeOriginalAscendingComparator INSTANCE = new ExifDateTimeOriginalAscendingComparator();

    @Override
    public int compare(File fileLeft, File fileRight) {
        long timeLeft = exifInfo.getTimeTakenInMillis(fileLeft);
        long timeRight = exifInfo.getTimeTakenInMillis(fileRight);

        return timeLeft == timeRight
                ? 0
                : timeLeft < timeRight
                ? -1
                : 1;
    }
}
