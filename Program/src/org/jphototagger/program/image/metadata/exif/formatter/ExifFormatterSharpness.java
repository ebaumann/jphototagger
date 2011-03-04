package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#SHARPNESS}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterSharpness extends ExifFormatter {
    public static final ExifFormatterSharpness INSTANCE = new ExifFormatterSharpness();
    private static final Map<Integer, String> EXIF_KEY_OF_SHARPNESS = new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_SHARPNESS.put(0, "SharpnessNormal");
        EXIF_KEY_OF_SHARPNESS.put(1, "SharpnessSoft");
        EXIF_KEY_OF_SHARPNESS.put(2, "SharpnessHard");
    }

    private ExifFormatterSharpness() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.SHARPNESS);

        if (ExifShort.byteCount() == exifTag.rawValue().length) {
            ExifShort es = new ExifShort(exifTag.rawValue(), exifTag.byteOrder());
            int value = es.value();

            if (EXIF_KEY_OF_SHARPNESS.containsKey(value)) {
                return translate(IfdType.EXIF, EXIF_KEY_OF_SHARPNESS.get(value));
            }
        }

        return "?";
    }
}
