package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifShort;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the type {@link ExifTag# }.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterWhiteBalance extends ExifFormatter {

    public static final ExifFormatterWhiteBalance INSTANCE =
            new ExifFormatterWhiteBalance();
    private static final Map<Integer, String> exifKeyOfWhiteBalance =
            new HashMap<Integer, String>();


    static {
        exifKeyOfWhiteBalance.put(0, "WhiteBalanceAutomatic"); // NOI18N
        exifKeyOfWhiteBalance.put(1, "WhiteBalanceManual"); // NOI18N
    }

    private ExifFormatterWhiteBalance() {
    }

    @Override
    public String format(IdfEntryProxy entry) {
        if (entry.getTag() != ExifTag.WHITE_BALANCE.getId())
            throw new IllegalArgumentException("Wrong tag: " + entry);
        if (ExifShort.isRawValueByteCountOk(entry.getRawValue())) {
            ExifShort es = new ExifShort(entry.getRawValue(),
                    entry.getByteOrder());
            int value = es.getValue();
            if (exifKeyOfWhiteBalance.containsKey(value)) {
                return translation.translate(exifKeyOfWhiteBalance.get(value));
            }
        }
        return "?";
    }
}
