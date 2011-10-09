package org.jphototagger.domain.metadata.exif;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
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

    /**
     *
     * @param file
     * @return First non empty collection of tags from an ExifInfo implementation
     */
    public static Collection<ExifTag> getExifTags(File file) {
        Collection<? extends ExifInfo> exifInfos = Lookup.getDefault().lookupAll(ExifInfo.class);

        for (ExifInfo exifInfo : exifInfos) {
            Collection<ExifTag> exifTags = exifInfo.getExifTags(file);
            if (!exifTags.isEmpty()) {
                return exifTags;
            }
        }

        return Collections.emptyList();
    }

    /**
     *
     * @param file
     * @return First non empty collection of tags from an ExifInfo implementation
     */
    public static Collection<ExifTag> getExifTagsPreferCached(File file) {
        Collection<? extends ExifInfo> exifInfos = Lookup.getDefault().lookupAll(ExifInfo.class);

        for (ExifInfo exifInfo : exifInfos) {
            Collection<ExifTag> exifTags = exifInfo.getExifTagsPreferCached(file);
            if (!exifTags.isEmpty()) {
                return exifTags;
            }
        }

        return Collections.emptyList();
    }

    private ExifUtil() {
    }
}
