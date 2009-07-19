package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifByteOrder;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Formats an EXIF entry of the type {@link ExifTag#GPS_TIME_STAMP}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterGpsTimeStamp extends ExifFormatter {

    public static final ExifFormatterGpsTimeStamp INSTANCE =
            new ExifFormatterGpsTimeStamp();

    private ExifFormatterGpsTimeStamp() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.GPS_TIME_STAMP.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        ExifByteOrder byteOrder = entry.getByteOrder();
        byte[] rawValue = entry.getRawValue();
        if (rawValue.length != 24)
            return new String(rawValue);
        ExifRational hours = new ExifRational(
                Arrays.copyOfRange(rawValue, 0, 8),
                byteOrder);
        ExifRational minutes = new ExifRational(
                Arrays.copyOfRange(rawValue, 8, 16),
                byteOrder);
        ExifRational seconds = new ExifRational(
                Arrays.copyOfRange(rawValue, 16, 24),
                byteOrder);
        int h = (int) ExifDatatypeUtil.toLong(hours);
        int m = (int) ExifDatatypeUtil.toLong(minutes);
        int s = (int) ExifDatatypeUtil.toLong(seconds);
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 4, 3, h, m, s);
        DateFormat df = DateFormat.getTimeInstance(DateFormat.LONG);
        return df.format(cal.getTime());
    }
}
