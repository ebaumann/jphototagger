package org.jphototagger.exif.formatter;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifIfd;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifShort;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#WHITE_BALANCE}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterWhiteBalance extends ExifFormatter {

    public static final ExifFormatterWhiteBalance INSTANCE = new ExifFormatterWhiteBalance();
    private static final Map<Integer, String> EXIF_KEY_OF_WHITE_BALANCE = new HashMap<>();

    static {
        EXIF_KEY_OF_WHITE_BALANCE.put(0, "WhiteBalanceAutomatic");
        EXIF_KEY_OF_WHITE_BALANCE.put(1, "WhiteBalanceManual");
    }

    private ExifFormatterWhiteBalance() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Properties.WHITE_BALANCE);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_WHITE_BALANCE.containsKey(value)) {
                return translate(ExifIfd.EXIF, EXIF_KEY_OF_WHITE_BALANCE.get(value));
            }
        }

        return "?";
    }
}
