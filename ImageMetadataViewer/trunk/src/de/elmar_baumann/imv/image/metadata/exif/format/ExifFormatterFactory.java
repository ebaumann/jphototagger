package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates instances of {@link ExifFormatter}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifFormatterFactory {

    private static final Map<Integer, ExifFormatter> formatterOfTagId =
            new HashMap<Integer, ExifFormatter>();


    static {
        // Ordered alphabetically for faster check
        formatterOfTagId.put(ExifTag.CONTRAST.getId(),
                ExifFormatterContrast.INSTANCE);
        formatterOfTagId.put(ExifTag.COPYRIGHT.getId(),
                ExifFormatterCopyright.INSTANCE);
        formatterOfTagId.put(ExifTag.DATE_TIME_ORIGINAL.getId(),
                ExifFormatterDateTime.INSTANCE);
        formatterOfTagId.put(ExifTag.EXPOSURE_PROGRAM.getId(),
                ExifFormatterExposureProgram.INSTANCE);
        formatterOfTagId.put(ExifTag.EXPOSURE_TIME.getId(),
                ExifFormatterExposureTime.INSTANCE);
        formatterOfTagId.put(ExifTag.FILE_SOURCE.getId(),
                ExifFormatterFileSource.INSTANCE);
        formatterOfTagId.put(ExifTag.FLASH.getId(), ExifFormatterFlash.INSTANCE);
        formatterOfTagId.put(ExifTag.FOCAL_LENGTH.getId(),
                ExifFormatterFocalLength.INSTANCE);
        formatterOfTagId.put(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId(),
                ExifFormatterFocalLengthIn35mm.INSTANCE);
        formatterOfTagId.put(ExifTag.F_NUMBER.getId(),
                ExifFormatterFnumber.INSTANCE);
        formatterOfTagId.put(ExifTag.GPS_DATE_STAMP.getId(),
                ExifFormatterGpsDateStamp.INSTANCE);
        formatterOfTagId.put(ExifTag.GPS_SATELLITES.getId(),
                ExifFormatterGpsSatellites.INSTANCE);
        formatterOfTagId.put(ExifTag.GPS_TIME_STAMP.getId(),
                ExifFormatterGpsTimeStamp.INSTANCE);
        formatterOfTagId.put(ExifTag.GPS_VERSION_ID.getId(),
                ExifFormatterGpsVersionId.INSTANCE);
        formatterOfTagId.put(ExifTag.ISO_SPEED_RATINGS.getId(),
                ExifFormatterIsoSpeedRatings.INSTANCE);
        formatterOfTagId.put(ExifTag.METERING_MODE.getId(),
                ExifFormatterMeteringMode.INSTANCE);
        formatterOfTagId.put(ExifTag.SATURATION.getId(),
                ExifFormatterSaturation.INSTANCE);
        formatterOfTagId.put(ExifTag.SHARPNESS.getId(),
                ExifFormatterSharpness.INSTANCE);
        formatterOfTagId.put(ExifTag.USER_COMMENT.getId(),
                ExifFormatterUserComment.INSTANCE);
        formatterOfTagId.put(ExifTag.WHITE_BALANCE.getId(),
                ExifFormatterWhiteBalance.INSTANCE);
        formatterOfTagId.put(ExifTag.ARTIST.getId(),
                ExifFormatterAscii.INSTANCE);
        formatterOfTagId.put(ExifTag.IMAGE_DESCRIPTION.getId(),
                ExifFormatterAscii.INSTANCE);
        formatterOfTagId.put(ExifTag.MAKE.getId(),
                ExifFormatterAscii.INSTANCE);
        formatterOfTagId.put(ExifTag.MODEL.getId(),
                ExifFormatterAscii.INSTANCE);
        formatterOfTagId.put(ExifTag.SOFTWARE.getId(),
                ExifFormatterAscii.INSTANCE);
    }

    /**
     * Returns a formatter for a specific EXIF tag.
     *
     * @param  tagId ID of the exif tag
     * @return formatter or null if no formatter exists for that tag ID
     */
    public static ExifFormatter get(int tagId) {
        return formatterOfTagId.get(tagId);
    }

    private ExifFormatterFactory() {
    }
}
