package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.metadata.exif.ExifFieldValueFormatter;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats an EXIF entry of the type {@link ExifTag#GPS_DATE_STAMP}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterGpsDateStamp extends ExifFormatter {

    public static final ExifFormatterGpsDateStamp INSTANCE =
            new ExifFormatterGpsDateStamp();

    private ExifFormatterGpsDateStamp() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.GPS_DATE_STAMP.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        byte[] rawValue = entry.getRawValue();
        String rawString = new String(rawValue);
        if (rawString.length() != 11)
            return rawString;
        try {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
            Date date = df.parse(rawString.substring(0, 10));
            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        } catch (ParseException ex) {
            AppLog.logWarning(ExifFieldValueFormatter.class, ex);
        }
        return rawString;
    }
}
