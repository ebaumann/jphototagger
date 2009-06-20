package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifByte;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.Arrays;

/**
 * Formats an EXIF entry of the type {@link ExifTag#GPS_VERSION_ID}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterGpsVersionId extends ExifFormatter {

    public static final ExifFormatterGpsVersionId INSTANCE =
            new ExifFormatterGpsVersionId();

    private ExifFormatterGpsVersionId() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.GPS_VERSION_ID.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        byte[] rawValue = entry.getRawValue();
        assert rawValue.length == 4 : rawValue.length;
        if (rawValue.length != 4)
            return new String(rawValue);
        ExifByte first = new ExifByte(Arrays.copyOfRange(rawValue, 0, 1));
        ExifByte second = new ExifByte(Arrays.copyOfRange(rawValue, 1, 2));
        ExifByte third = new ExifByte(Arrays.copyOfRange(rawValue, 2, 3));
        ExifByte fourth = new ExifByte(Arrays.copyOfRange(rawValue, 3, 4));

        return first.getValue() +
                "." + second.getValue() +
                "." + third.getValue() +
                "." + fourth.getValue();
    }
}
