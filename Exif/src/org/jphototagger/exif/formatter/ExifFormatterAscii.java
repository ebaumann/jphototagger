package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifAscii;
import org.jphototagger.exif.datatype.ExifDataType;

/**
 * Formats EXIF metadata fields in ASCII format.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterAscii extends ExifFormatter {

    public static final ExifFormatterAscii INSTANCE = new ExifFormatterAscii();

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifDataType(exifTag, ExifDataType.ASCII);

        return ExifAscii.convertRawValueToString(exifTag.getRawValue());
    }
}
