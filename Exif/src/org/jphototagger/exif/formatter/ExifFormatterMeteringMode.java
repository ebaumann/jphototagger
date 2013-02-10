package org.jphototagger.exif.formatter;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifIfd;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifShort;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Properties#METERING_MODE}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterMeteringMode extends ExifFormatter {

    public static final ExifFormatterMeteringMode INSTANCE = new ExifFormatterMeteringMode();
    private static final Map<Integer, String> EXIF_KEY_OF_METERING_MODE = new HashMap<>();

    static {
        EXIF_KEY_OF_METERING_MODE.put(0, "MeteringModeUnknown");
        EXIF_KEY_OF_METERING_MODE.put(1, "MeteringModeIntegral");
        EXIF_KEY_OF_METERING_MODE.put(2, "MeteringModeIntegralCenter");
        EXIF_KEY_OF_METERING_MODE.put(3, "MeteringModeSpot");
        EXIF_KEY_OF_METERING_MODE.put(4, "MeteringModeMultiSpot");
        EXIF_KEY_OF_METERING_MODE.put(5, "MeteringModeMatrix");
        EXIF_KEY_OF_METERING_MODE.put(6, "MeteringModeSelective");
    }

    private ExifFormatterMeteringMode() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Properties.METERING_MODE);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_METERING_MODE.containsKey(value)) {
                return translate(ExifIfd.EXIF, EXIF_KEY_OF_METERING_MODE.get(value));
            }
        }

        return "?";
    }
}
