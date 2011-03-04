package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;

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

        if (ExifShort.byteCountOk(exifTag.rawValue())) {
            ExifShort es = new ExifShort(exifTag.rawValue(), exifTag.byteOrder());
            int value = es.value();

            if (EXIF_KEY_OF_WHITE_BALANCE.containsKey(value)) {
                return translate(IfdType.EXIF, EXIF_KEY_OF_WHITE_BALANCE.get(value));
            }
        }

        return "?";
    }
}
