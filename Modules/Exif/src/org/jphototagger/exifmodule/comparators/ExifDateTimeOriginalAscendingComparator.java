package org.jphototagger.exifmodule.comparators;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.ClassEquality;
import org.jphototagger.xmp.XmpMetadata;
import org.openide.util.Lookup;

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
            return findDateCreated(imageFile);
        }
        return exif.getDateTimeOriginal().getTime();
    }

    /**
     * @param file
     * @return {@link XmpIptc4XmpCoreDateCreatedMetaDataValue} if contained in XMP of file and valid, file's last
     * modification date else
     */
    static long findDateCreated(File file) {
        Xmp xmp;
        try {
            xmp = XmpMetadata.getXmpFromSidecarFileOf(file);
        } catch (IOException ex) {
            Logger.getLogger(ExifDateTimeOriginalAscendingComparator.class.getName()).log(Level.SEVERE, null, ex);
            return file.lastModified();
        }
        if (xmp == null || !xmp.contains(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE)) {
            return file.lastModified();
        }
        String date = (String) xmp.getValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        Long timestamp = XmpIptc4XmpCoreDateCreatedMetaDataValue.createTimestamp(date);
        return timestamp == null
                ? file.lastModified()
                : timestamp;
    }

    @Override
    public String toString() {
        return "Exif DateTimeOriginal only Date Ascending";
    }
}
