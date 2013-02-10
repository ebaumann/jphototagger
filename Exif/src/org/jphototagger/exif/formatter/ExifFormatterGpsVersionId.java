package org.jphototagger.exif.formatter;

import java.util.Arrays;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifByte;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#GPS_VERSION_ID}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsVersionId extends ExifFormatter {

    public static final ExifFormatterGpsVersionId INSTANCE = new ExifFormatterGpsVersionId();

    private ExifFormatterGpsVersionId() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Properties.GPS_VERSION_ID);

        byte[] rawValue = exifTag.getRawValue();

        assert rawValue.length == 4 : rawValue.length;

        if (rawValue.length != 4) {
            return new String(rawValue);
        }

        ExifByte first = new ExifByte(Arrays.copyOfRange(rawValue, 0, 1));
        ExifByte second = new ExifByte(Arrays.copyOfRange(rawValue, 1, 2));
        ExifByte third = new ExifByte(Arrays.copyOfRange(rawValue, 2, 3));
        ExifByte fourth = new ExifByte(Arrays.copyOfRange(rawValue, 3, 4));

        return first.getValue() + "." + second.getValue() + "." + third.getValue() + "." + fourth.getValue();
    }
}
