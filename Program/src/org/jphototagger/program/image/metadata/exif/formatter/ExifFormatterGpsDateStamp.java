package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTagValueFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#GPS_DATE_STAMP}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterGpsDateStamp extends ExifFormatter {
    public static final ExifFormatterGpsDateStamp INSTANCE = new ExifFormatterGpsDateStamp();

    private ExifFormatterGpsDateStamp() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.GPS_DATE_STAMP);

        byte[] rawValue = exifTag.rawValue();
        String rawString = new String(rawValue);

        if (rawString.length() != 11) {
            return rawString;
        }

        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
            Date date = df.parse(rawString.substring(0, 10));

            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        } catch (Exception ex) {
            AppLogger.logSevere(ExifTagValueFormatter.class, ex);
        }

        return rawString;
    }
}
