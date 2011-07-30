package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.domain.exif.ExifIfdType;
import org.jphototagger.domain.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#WHITE_BALANCE}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterWhiteBalance extends ExifFormatter {
    public static final ExifFormatterWhiteBalance INSTANCE = new ExifFormatterWhiteBalance();
    private static final Map<Integer, String> EXIF_KEY_OF_WHITE_BALANCE = new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_WHITE_BALANCE.put(0, "WhiteBalanceAutomatic");
        EXIF_KEY_OF_WHITE_BALANCE.put(1, "WhiteBalanceManual");
    }

    private ExifFormatterWhiteBalance() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.WHITE_BALANCE);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_WHITE_BALANCE.containsKey(value)) {
                return translate(ExifIfdType.EXIF, EXIF_KEY_OF_WHITE_BALANCE.get(value));
            }
        }

        return "?";
    }
}
