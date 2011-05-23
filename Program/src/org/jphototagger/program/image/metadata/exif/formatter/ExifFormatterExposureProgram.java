package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.datatype.ExifShort;
import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#EXPOSURE_PROGRAM}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterExposureProgram extends ExifFormatter {
    public static final ExifFormatterExposureProgram INSTANCE = new ExifFormatterExposureProgram();
    private static final Map<Integer, String> EXIF_KEY_OF_EXPOSURE_PROGRAM = new HashMap<Integer, String>();

    static {
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(0, "ExposureProgramUnkonwn");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(1, "ExposureProgramManual");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(2, "ExposureProgramNormalProgram");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(3, "ExposureProgramAperturePriority");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(4, "ExposureProgramTimePriority");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(5, "ExposureProgramCreativ");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(6, "ExposureProgramAction");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(7, "ExposureProgramPortrait");
        EXIF_KEY_OF_EXPOSURE_PROGRAM.put(8, "ExposureProgramLandscape");
    }

    private ExifFormatterExposureProgram() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.EXPOSURE_PROGRAM);

        if (ExifShort.isRawValueByteCountOk(exifTag.getRawValue())) {
            ExifShort es = new ExifShort(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            int value = es.getValue();

            if (EXIF_KEY_OF_EXPOSURE_PROGRAM.containsKey(value)) {
                return translate(IfdType.EXIF, EXIF_KEY_OF_EXPOSURE_PROGRAM.get(value));
            }
        }

        return "?";
    }
}
