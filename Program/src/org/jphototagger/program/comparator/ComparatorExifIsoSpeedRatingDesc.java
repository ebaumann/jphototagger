package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.domain.exif.Exif;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifIsoSpeedRatingDesc extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -3791741623919608627L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft = repo.findExifOfImageFile(fileLeft);
        Exif exifRight = repo.findExifOfImageFile(fileRight);

        return ((exifLeft == null) && (exifRight == null))
                ? 0
                : ((exifLeft == null) && (exifRight != null))
                ? 1
                : ((exifLeft != null) && (exifRight == null))
                ? -1
                : exifRight.getIsoSpeedRatings() - exifLeft.getIsoSpeedRatings();
    }
}
