package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifAscii;
import org.jphototagger.program.image.metadata.exif.datatype.ExifDataType;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;

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
