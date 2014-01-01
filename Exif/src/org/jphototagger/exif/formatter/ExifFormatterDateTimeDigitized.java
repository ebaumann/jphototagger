package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#DATE_TIME_ORIGINAL}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterDateTimeDigitized extends ExifFormatter {

    public static final ExifFormatterDateTimeDigitized INSTANCE = new ExifFormatterDateTimeDigitized();

    private ExifFormatterDateTimeDigitized() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }
        Ensure.exifTagId(exifTag, ExifTag.Properties.DATE_TIME_DIGITIZED);
        return ExifDateTimeFormatter.format(exifTag.getRawValue());
    }
}
