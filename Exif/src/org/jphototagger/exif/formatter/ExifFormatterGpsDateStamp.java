package org.jphototagger.exif.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_DATE_STAMP}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsDateStamp extends ExifFormatter {

    public static final ExifFormatterGpsDateStamp INSTANCE = new ExifFormatterGpsDateStamp();

    private ExifFormatterGpsDateStamp() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_DATE_STAMP);

        byte[] rawValue = exifTag.getRawValue();
        String rawString = new String(rawValue);

        if (rawString.length() != 11) {
            return rawString;
        }

        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
            Date date = df.parse(rawString.substring(0, 10));

            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        } catch (Exception ex) {
            Logger.getLogger(ExifFormatterGpsDateStamp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rawString;
    }
}
