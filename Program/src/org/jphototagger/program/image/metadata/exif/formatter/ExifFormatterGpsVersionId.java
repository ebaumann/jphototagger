package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifByte;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.util.Arrays;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_VERSION_ID}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsVersionId extends ExifFormatter {
    public static final ExifFormatterGpsVersionId INSTANCE = new ExifFormatterGpsVersionId();

    private ExifFormatterGpsVersionId() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_VERSION_ID);

        byte[] rawValue = exifTag.rawValue();

        assert rawValue.length == 4 : rawValue.length;

        if (rawValue.length != 4) {
            return new String(rawValue);
        }

        ExifByte first = new ExifByte(Arrays.copyOfRange(rawValue, 0, 1));
        ExifByte second = new ExifByte(Arrays.copyOfRange(rawValue, 1, 2));
        ExifByte third = new ExifByte(Arrays.copyOfRange(rawValue, 2, 3));
        ExifByte fourth = new ExifByte(Arrays.copyOfRange(rawValue, 3, 4));

        return first.value() + "." + second.value() + "." + third.value() + "." + fourth.value();
    }
}
