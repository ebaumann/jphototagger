package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.domain.exif.Exif;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifFocalLengthAsc extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -3547538589063666249L;
    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft = repo.getExifOfImageFile(fileLeft);
        Exif exifRight = repo.getExifOfImageFile(fileRight);

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
}
