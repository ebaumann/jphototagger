package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#SATURATION}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterSaturation extends ExifFormatter {
    public static final ExifFormatterSaturation INSTANCE =
        new ExifFormatterSaturation();
    private static final Map<Integer, String> exifKeyOfSaturation =
        new HashMap<Integer, String>();

    static {
        exifKeyOfSaturation.put(0, "SaturationNormal");
        exifKeyOfSaturation.put(1, "SaturationLow");
        exifKeyOfSaturation.put(2, "SaturationHigh");
    }

    private ExifFormatterSaturation() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.SATURATION);

        if (ExifShort.byteCountOk(exifTag.rawValue())) {
            ExifShort es = new ExifShort(exifTag.rawValue(),
                                         exifTag.byteOrder());
            int value = es.value();

            if (exifKeyOfSaturation.containsKey(value)) {
                return translate(IfdType.EXIF, exifKeyOfSaturation.get(value));
            }
        }

        return "?";
    }
}
