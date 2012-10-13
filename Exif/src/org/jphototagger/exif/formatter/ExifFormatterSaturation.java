package org.jphototagger.exif.formatter;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifShort;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#SATURATION}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterSaturation extends ExifFormatter {

    public static final ExifFormatterSaturation INSTANCE = new ExifFormatterSaturation();
    private static final Map<Integer, String> exifKeyOfSaturation = new HashMap<>();

    static {
        exifKeyOfSaturation.put(0, "SaturationNormal");
        exifKeyOfSaturation.put(1, "SaturationLow");
        exifKeyOfSaturation.put(2, "SaturationHigh");
    }

    private ExifFormatterSaturation() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.SATURATION);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (exifKeyOfSaturation.containsKey(value)) {
                return translate(ExifIfdType.EXIF, exifKeyOfSaturation.get(value));
            }
        }

        return "?";
    }
}
