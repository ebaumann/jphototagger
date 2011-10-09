package org.jphototagger.domain.metadata.exif;

import java.io.File;
import java.util.Collection;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifUtil {

    /**
     * Lookups all aviable ExifReaders and if any did read the EXIF it
     * will be returned.
     *
     * @param file
     * @return
     */
    public static Exif readExif(File file) {
        Collection<? extends ExifReader> exifReaders = Lookup.getDefault().lookupAll(ExifReader.class);
        for (ExifReader exifReader : exifReaders) {
            Exif readExif = null;
            if (exifReader.canReadExif(file)) {
                readExif = exifReader.readExif(file);
            }
            if (readExif != null) {
                return readExif;
            }
        }
        return null;
    }

    /**
     * Lookups all aviable ExifReaders and if any did read the EXIF it
     * will be returned.
     *
     * @param file
     * @return
     */
    public static Exif readExifPreferCached(File file) {
        Collection<? extends ExifReader> exifReaders = Lookup.getDefault().lookupAll(ExifReader.class);
        for (ExifReader exifReader : exifReaders) {
            Exif readExif = null;
            if (exifReader.canReadExif(file)) {
                readExif = exifReader.readExifPreferCached(file);
            }
            if (readExif != null) {
                return readExif;
            }
        }
        return null;
    }

    private ExifUtil() {
    }
}
