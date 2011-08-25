package org.jphototagger.program.comparator;

import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.domain.exif.Exif;
import java.io.File;
import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ComparatorExifRecordingEquipmentAsc extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = -7653829996215237671L;
    private transient Collator collator = Collator.getInstance();
    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        Exif exifLeft = repo.getExifOfImageFile(fileLeft);
        Exif exifRight = repo.getExifOfImageFile(fileRight);
        String eqipLeft = (exifLeft == null)
                ? null
                : exifLeft.getRecordingEquipment();
        String eqipRight = (exifRight == null)
                ? null
                : exifRight.getRecordingEquipment();

        return ((eqipLeft == null) && (eqipRight == null))
                ? 0
                : ((eqipLeft == null) && (eqipRight != null))
                ? -1
                : ((eqipLeft != null) && (eqipRight == null))
                ? 1
                : collator.compare(eqipLeft, eqipRight);
    }
}
