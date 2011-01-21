package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.ExifTag;

/**
 * Formats a raw value of an EXIF tag (Generic raw value formatter).
 *
 * @author Elmar Baumann
 */
public interface ExifRawValueFormatter {

    /**
     * Formats a raw value of an EXIF tag.
     *
     * @param exifTag tag to format
     * @return        formatted string
     */
    String format(ExifTag exifTag);
}
