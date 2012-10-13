package org.jphototagger.exif.formatter;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifIfdType;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifShort;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#SHARPNESS}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterSharpness extends ExifFormatter {

    public static final ExifFormatterSharpness INSTANCE = new ExifFormatterSharpness();
    private static final Map<Integer, String> EXIF_KEY_OF_SHARPNESS = new HashMap<>();

    static {
        EXIF_KEY_OF_SHARPNESS.put(0, "SharpnessNormal");
        EXIF_KEY_OF_SHARPNESS.put(1, "SharpnessSoft");
        EXIF_KEY_OF_SHARPNESS.put(2, "SharpnessHard");
    }

    private ExifFormatterSharpness() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.SHARPNESS);

        if (ExifShort.getRawValueByteCount() == exifTag.getRawValue().length) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_SHARPNESS.containsKey(value)) {
                return translate(ExifIfdType.EXIF, EXIF_KEY_OF_SHARPNESS.get(value));
            }
        }

        return "?";
    }
}
