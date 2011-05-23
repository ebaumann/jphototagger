package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.image.metadata.exif.formatter.ExifFormatter;
import org.jphototagger.program.image.metadata.exif.formatter.ExifFormatterFactory;

/**
 *
 * @author Elmar Baumann
 */
public final class ExifTagValueFormatter {

    /**
     * Formatis an EXIF tag value.
     *
     * @param  exifTag tag
     * @return         formatted tag value
     */
    public static String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        ExifFormatter formatter = ExifFormatterFactory.get(exifTag);

        if (formatter != null) {
            return formatter.format(exifTag);
        }

        return exifTag.getStringValue().trim();
    }

    private ExifTagValueFormatter() {}
}
