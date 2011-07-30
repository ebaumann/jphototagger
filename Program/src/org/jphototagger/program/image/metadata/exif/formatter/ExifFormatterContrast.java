package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.domain.exif.ExifIfdType;
import org.jphototagger.domain.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#CONTRAST}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterContrast extends ExifFormatter {
    public static final ExifFormatterContrast INSTANCE = new ExifFormatterContrast();
    private static final Map<Integer, String> EXIF_KEY_OF_CONTRAST = new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_CONTRAST.put(0, "ContrastNormal");
        EXIF_KEY_OF_CONTRAST.put(1, "ContrastLow");
        EXIF_KEY_OF_CONTRAST.put(2, "ContrastHigh");
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.CONTRAST);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_CONTRAST.containsKey(value)) {
                return translate(ExifIfdType.EXIF, EXIF_KEY_OF_CONTRAST.get(value));
            }
        }

        return "?";
    }

    private ExifFormatterContrast() {}
}
