package org.jphototagger.domain.metadata.exif;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface ExifReader {

    /**
     *
     * @param file
     * @return EXIF embedded in file or null
     */
    Exif readExif(File file);

    Exif readExifPreferCached(File file);

    boolean canReadExif(File file);
}
