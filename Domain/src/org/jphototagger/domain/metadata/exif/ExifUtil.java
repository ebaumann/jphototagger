package org.jphototagger.domain.metadata.exif;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifUtil {

    /**
     * Lookups all aviable ExifReaders and if any did read the EXIF, that will be returned.
     *
     * @param file
     * @return EXIF or null
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
     * Lookups all aviable ExifReaders and if any did read the EXIF, that will be returned.
     *
     * @param file
     * @return EXIF or null
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
     * @return collection of tags of all ExifInfo implementations, maybe empty
     */
    public static Collection<ExifTag> getExifTags(File file) {
        Collection<? extends ExifInfo> exifInfos = Lookup.getDefault().lookupAll(ExifInfo.class);
        Collection<ExifTag> exifTags = new LinkedList<ExifTag>();

        for (ExifInfo exifInfo : exifInfos) {
            exifTags.addAll(exifInfo.getExifTags(file));
        }

        return exifTags;
    }

    /**
     *
     * @param file
     * @return collection of tags of all ExifInfo implementations, maybe empty
     */
    public static Collection<ExifTag> getExifTagsPreferCached(File file) {
        Collection<? extends ExifInfo> exifInfos = Lookup.getDefault().lookupAll(ExifInfo.class);
        Collection<ExifTag> exifTags = new LinkedList<ExifTag>();

        for (ExifInfo exifInfo : exifInfos) {
            exifTags.addAll(exifInfo.getExifTagsPreferCached(file));
        }

        return exifTags;
    }

    private ExifUtil() {
    }
}
