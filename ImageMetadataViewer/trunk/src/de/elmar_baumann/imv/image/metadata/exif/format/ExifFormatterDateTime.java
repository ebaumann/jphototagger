package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifAscii;
import de.elmar_baumann.imv.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DateFormat;
import java.util.GregorianCalendar;

/**
 * Formats an EXIF entry of the type {@link ExifTag# }.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterDateTime extends ExifFormatter {

    public static final ExifFormatterDateTime INSTANCE =
            new ExifFormatterDateTime();

    private ExifFormatterDateTime() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.DATE_TIME_ORIGINAL.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        byte[] rawValue = entry.getRawValue();
        String value = ExifAscii.decode(rawValue);
        if (value.length() >= 18) {
            try {
                int year = Integer.parseInt(value.substring(0, 4));
                int month = Integer.parseInt(value.substring(5, 7));
                int day = Integer.parseInt(value.substring(8, 10));
                int hour = Integer.parseInt(value.substring(11, 13));
                int minute = Integer.parseInt(value.substring(14, 16));
                int second = Integer.parseInt(value.substring(17, 19));
                GregorianCalendar cal = new GregorianCalendar(
                        year, month - 1, day, hour, minute, second);
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                        DateFormat.FULL);
                return df.format(cal.getTime());
            } catch (Exception ex) {
                AppLog.logWarning(ExifFieldValueFormatter.class, ex);
            }
        }
        return value;
    }
}
