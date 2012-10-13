package org.jphototagger.exifmodule.comparators;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.ClassEquality;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifFocalLengthAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft = repo.findExifOfImageFile(fileLeft);
        Exif exifRight = repo.findExifOfImageFile(fileRight);

        return ((exifLeft == null) && (exifRight == null))
                ? 0
                : ((exifLeft == null) && (exifRight != null))
                ? -1
                : ((exifLeft != null) && (exifRight == null))
                ? 1
                : (exifLeft.getFocalLength() > exifRight.getFocalLength())
                ? 1
                : (exifLeft.getFocalLength() == exifRight.getFocalLength())
                ? 0
                : -1;
    }

    @Override
    public String toString() {
        return "EXIF Focal Length Ascending";
    }
}
