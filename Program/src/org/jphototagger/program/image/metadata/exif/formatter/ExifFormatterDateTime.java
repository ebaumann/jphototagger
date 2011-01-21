package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.metadata.exif.datatype.ExifAscii;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTagValueFormatter;

import java.text.DateFormat;

import java.util.GregorianCalendar;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#DATE_TIME_ORIGINAL}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterDateTime extends ExifFormatter {
    public static final ExifFormatterDateTime INSTANCE =
        new ExifFormatterDateTime();

    private ExifFormatterDateTime() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.DATE_TIME_ORIGINAL);

        byte[] rawValue = exifTag.rawValue();
        String value    = ExifAscii.decode(rawValue).trim();

        if (value.length() >= 18) {
            try {
                int               year   = Integer.parseInt(value.substring(0,
                                               4));
                int               month  = Integer.parseInt(value.substring(5,
                                               7));
                int               day    = Integer.parseInt(value.substring(8,
                                               10));
                int               hour   = Integer.parseInt(value.substring(11,
                                               13));
                int               minute = Integer.parseInt(value.substring(14,
                                               16));
                int               second = Integer.parseInt(value.substring(17,
                                               19));
                GregorianCalendar cal    = new GregorianCalendar(year,
                                               month - 1, day, hour, minute,
                                               second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                                    DateFormat.FULL);

                return df.format(cal.getTime());
            } catch (Exception ex) {
                AppLogger.logSevere(ExifTagValueFormatter.class, ex);
            }
        }

        return value;
    }
}
