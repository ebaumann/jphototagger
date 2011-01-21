package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#CONTRAST}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterContrast extends ExifFormatter {
    public static final ExifFormatterContrast INSTANCE =
        new ExifFormatterContrast();
    private static final Map<Integer, String> EXIF_KEY_OF_CONTRAST =
        new HashMap<Integer, String>();

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

        if (ExifShort.byteCountOk(exifTag.rawValue())) {
            ExifShort es = new ExifShort(exifTag.rawValue(),
                                         exifTag.byteOrder());
            int value = es.value();

            if (EXIF_KEY_OF_CONTRAST.containsKey(value)) {
                return translate(IfdType.EXIF, EXIF_KEY_OF_CONTRAST.get(value));
            }
        }

        return "?";
    }

    private ExifFormatterContrast() {}
}
