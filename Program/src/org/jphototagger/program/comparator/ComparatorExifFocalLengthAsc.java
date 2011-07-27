package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.domain.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifFocalLengthAsc extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = -3547538589063666249L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft = DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileLeft);
        Exif exifRight = DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileRight);

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
