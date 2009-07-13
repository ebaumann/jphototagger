package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;

/**
 * Formats an EXIF entry of the type {@link ExifTag#ISO_SPEED_RATINGS}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterIsoSpeedRatings extends ExifFormatter {

    public static final ExifFormatterIsoSpeedRatings INSTANCE =
            new ExifFormatterIsoSpeedRatings();
    private static final String postfix = " ISO";

    private ExifFormatterIsoSpeedRatings() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.ISO_SPEED_RATINGS.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            return Integer.toString(es.getValue()) + postfix;
        }
        return "?" + postfix;
    }
}
