package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#DATE_TIME_ORIGINAL}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterDateTime extends ExifFormatter {

    public static final ExifFormatterDateTime INSTANCE = new ExifFormatterDateTime();

    private ExifFormatterDateTime() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }
        Ensure.exifTagId(exifTag, ExifTag.Id.DATE_TIME);
        return ExifDateTimeFormatter.format(exifTag.getRawValue());
    }
}
