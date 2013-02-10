package org.jphototagger.exif.formatter;

import java.nio.ByteOrder;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifValueUtil;
import org.jphototagger.exif.datatype.ExifRational;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#GPS_TIME_STAMP}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsTimeStamp extends ExifFormatter {

    public static final ExifFormatterGpsTimeStamp INSTANCE = new ExifFormatterGpsTimeStamp();

    private ExifFormatterGpsTimeStamp() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Properties.GPS_TIME_STAMP);

        ByteOrder byteOrder = exifTag.convertByteOrderIdToByteOrder();
        byte[] rawValue = exifTag.getRawValue();

        if (rawValue.length != 24) {
            return new String(rawValue);
        }

        ExifRational hours = new ExifRational(Arrays.copyOfRange(rawValue, 0, 8), byteOrder);
        ExifRational minutes = new ExifRational(Arrays.copyOfRange(rawValue, 8, 16), byteOrder);
        ExifRational seconds = new ExifRational(Arrays.copyOfRange(rawValue, 16, 24), byteOrder);
        int h = (int) ExifValueUtil.convertExifRationalToLong(hours);
        int m = (int) ExifValueUtil.convertExifRationalToLong(minutes);
        int s = (int) ExifValueUtil.convertExifRationalToLong(seconds);
        Calendar cal = Calendar.getInstance();

        cal.set(2009, 4, 3, h, m, s);

        DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG);

        return df.format(cal.getTime());
    }
}
