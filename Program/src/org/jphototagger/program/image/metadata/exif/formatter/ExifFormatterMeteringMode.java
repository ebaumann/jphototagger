package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#METERING_MODE}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterMeteringMode extends ExifFormatter {
    public static final ExifFormatterMeteringMode INSTANCE = new ExifFormatterMeteringMode();
    private static final Map<Integer, String> EXIF_KEY_OF_METERING_MODE = new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_METERING_MODE.put(0, "MeteringModeUnknown");
        EXIF_KEY_OF_METERING_MODE.put(1, "MeteringModeIntegral");
        EXIF_KEY_OF_METERING_MODE.put(2, "MeteringModeIntegralCenter");
        EXIF_KEY_OF_METERING_MODE.put(3, "MeteringModeSpot");
        EXIF_KEY_OF_METERING_MODE.put(4, "MeteringModeMultiSpot");
        EXIF_KEY_OF_METERING_MODE.put(5, "MeteringModeMatrix");
        EXIF_KEY_OF_METERING_MODE.put(6, "MeteringModeSelective");
    }

    private ExifFormatterMeteringMode() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.METERING_MODE);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_METERING_MODE.containsKey(value)) {
                return translate(IfdType.EXIF, EXIF_KEY_OF_METERING_MODE.get(value));
            }
        }

        return "?";
    }
}
