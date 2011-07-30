package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.domain.exif.ExifIfdType;
import org.jphototagger.domain.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#FILE_SOURCE}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFileSource extends ExifFormatter {
    public static final ExifFormatterFileSource INSTANCE = new ExifFormatterFileSource();

    private ExifFormatterFileSource() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.FILE_SOURCE);

        byte[] rawValue = exifTag.getRawValue();

        if (rawValue.length >= 1) {
            int value = rawValue[0];

            if (value == 3) {
                return translate(ExifIfdType.EXIF, "FileSourceDigitalCamera");
            }
        }

        return "?";
    }
}
