package org.jphototagger.program.module.xmp.comparators;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.domain.metadata.exif.ExifUtil;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.util.ClassEquality;

/**
 *
 * @author Elmar Baumann
 */
public final class XmpExifDateTimeAscendingComparator extends ClassEquality implements Comparator<File>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);
    private final ExifInfo exifInfo = Lookup.getDefault().lookup(ExifInfo.class);

    @Override
    public int compare(File fileLeft, File fileRight) {
        String timeLeft = getTimeString(fileLeft);
        String timeRight = getTimeString(fileRight);

        return timeLeft.compareTo(timeRight);
    }

    private String getTimeString(File file) {
        Exif exif = ExifUtil.readExifPreferCached(file);
        boolean hasExif = exif != null && exif.getDateTimeOriginal() != null;

        if (hasExif) {
            Date exifDate = new Date(exifInfo.getTimeTakenInMillis(file));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(exifDate);
        }

        String xmpDate = repo.findXmpIptc4CoreDateCreated(file);

        return xmpDate != null ? xmpDate : "";
    }
}
