package org.jphototagger.domain.metadata.exif;

import java.io.File;
import java.util.Collection;

/**
 * @author Elmar Baumann
 */
public interface ExifInfo {

    /**
     *
     * @param file
     * @return range between 0.0 and 360.0
     */
    double getRotationAngleOfEmbeddedThumbnail(File file);

    /**
     *
     * @param file
     * @return tags or empty collection
     */
    Collection<ExifTag> getExifTags(File file);

    /**
     *
     * @param file
     * @return tags or empty collection
     */
    Collection<ExifTag> getExifTagsPreferCached(File file);
}
