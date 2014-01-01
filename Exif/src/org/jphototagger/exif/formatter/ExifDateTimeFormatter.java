package org.jphototagger.exif.formatter;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.exif.datatype.ExifAscii;

/**
 * @author Elmar Baumann
 */
public final class ExifDateTimeFormatter {

    static String format(byte[] rawValue) {
        String stringValue = ExifAscii.convertRawValueToString(rawValue).trim();
        if (stringValue.length() >= 18) {
            try {
                int year = Integer.parseInt(stringValue.substring(0, 4));
                int month = Integer.parseInt(stringValue.substring(5, 7));
                int day = Integer.parseInt(stringValue.substring(8, 10));
                int hour = Integer.parseInt(stringValue.substring(11, 13));
                int minute = Integer.parseInt(stringValue.substring(14, 16));
                int second = Integer.parseInt(stringValue.substring(17, 19));
                GregorianCalendar cal = new GregorianCalendar(year, month - 1, day, hour, minute, second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                return df.format(cal.getTime());
            } catch (Throwable t) {
                Logger.getLogger(ExifFormatterDateTimeOriginal.class.getName()).log(Level.SEVERE, null, t);
            }
        }
        return stringValue;
    }

    private ExifDateTimeFormatter() {
    }
}
