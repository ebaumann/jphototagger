package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.nio.ByteOrder;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_TIME_STAMP}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsTimeStamp extends ExifFormatter {
    public static final ExifFormatterGpsTimeStamp INSTANCE =
        new ExifFormatterGpsTimeStamp();

    private ExifFormatterGpsTimeStamp() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_TIME_STAMP);

        ByteOrder byteOrder = exifTag.byteOrder();
        byte[]    rawValue  = exifTag.rawValue();

        if (rawValue.length != 24) {
            return new String(rawValue);
        }

        ExifRational hours = new ExifRational(Arrays.copyOfRange(rawValue, 0,
                                 8), byteOrder);
        ExifRational minutes = new ExifRational(Arrays.copyOfRange(rawValue, 8,
                                   16), byteOrder);
        ExifRational seconds = new ExifRational(Arrays.copyOfRange(rawValue,
                                   16, 24), byteOrder);
        int      h   = (int) ExifDatatypeUtil.toLong(hours);
        int      m   = (int) ExifDatatypeUtil.toLong(minutes);
        int      s   = (int) ExifDatatypeUtil.toLong(seconds);
        Calendar cal = Calendar.getInstance();

        cal.set(2009, 4, 3, h, m, s);

        DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG);

        return df.format(cal.getTime());
    }
}
