package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifFocalLengthDesc extends ClassEquality implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 8930101703487566400L;

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft = DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileLeft);
        Exif exifRight = DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileRight);

        return ((exifLeft == null) && (exifRight == null))
               ? 0
               : ((exifLeft == null) && (exifRight != null))
                 ? 1
                 : ((exifLeft != null) && (exifRight == null))
                   ? -1
                   : (exifRight.getFocalLength() > exifLeft.getFocalLength())
                     ? 1
                     : (exifRight.getFocalLength() == exifLeft.getFocalLength())
                       ? 0
                       : -1;
    }
}
