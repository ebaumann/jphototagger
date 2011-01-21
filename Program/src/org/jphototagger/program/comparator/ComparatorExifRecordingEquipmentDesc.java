package org.jphototagger.program.comparator;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.lib.util.ClassEquality;

import java.io.File;
import java.io.Serializable;

import java.text.Collator;

import java.util.Comparator;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifRecordingEquipmentDesc extends ClassEquality
        implements Comparator<File>, Serializable {
    private static final long  serialVersionUID = -4021823021223274217L;
    private transient Collator collator         = Collator.getInstance();

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft =
            DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileLeft);
        Exif exifRight =
            DatabaseImageFiles.INSTANCE.getExifOfImageFile(fileRight);
        String eqipLeft  = (exifLeft == null)
                           ? null
                           : exifLeft.getRecordingEquipment();
        String eqipRight = (exifRight == null)
                           ? null
                           : exifRight.getRecordingEquipment();

        return ((eqipLeft == null) && (eqipRight == null))
               ? 0
               : ((eqipLeft == null) && (eqipRight != null))
                 ? 1
                 : ((eqipLeft != null) && (eqipRight == null))
                   ? -1
                   : collator.compare(eqipRight, eqipLeft);
    }
}
