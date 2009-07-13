package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag#WHITE_BALANCE}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterWhiteBalance extends ExifFormatter {

    public static final ExifFormatterWhiteBalance INSTANCE =
            new ExifFormatterWhiteBalance();
    private static final Map<Integer, String> EXIF_KEY_OF_WHITE_BALANCE =
            new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_WHITE_BALANCE.put(0, "WhiteBalanceAutomatic"); // NOI18N
        EXIF_KEY_OF_WHITE_BALANCE.put(1, "WhiteBalanceManual"); // NOI18N
    }

    private ExifFormatterWhiteBalance() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.WHITE_BALANCE.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry); // NOI18N
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (EXIF_KEY_OF_WHITE_BALANCE.containsKey(value)) {
                return TRANSLATION.translate(
                        EXIF_KEY_OF_WHITE_BALANCE.get(value));
            }
        }
        return "?"; // NOI18N
    }
}
