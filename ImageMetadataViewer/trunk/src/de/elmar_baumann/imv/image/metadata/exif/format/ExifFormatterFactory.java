package de.elmar_baumann.imv.image.metadata.exif.format;

import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates instances of {@link ExifFormatter}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterFactory {

    private static final Map<Integer, ExifFormatter> FORMATTER_OF_TAG_ID =
            new HashMap<Integer, ExifFormatter>();

    static {
        // Ordered alphabetically for faster check
        FORMATTER_OF_TAG_ID.put(ExifTag.CONTRAST.getId(),
                ExifFormatterContrast.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.COPYRIGHT.getId(),
                ExifFormatterCopyright.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.DATE_TIME_ORIGINAL.getId(),
                ExifFormatterDateTime.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.EXPOSURE_PROGRAM.getId(),
                ExifFormatterExposureProgram.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.EXPOSURE_TIME.getId(),
                ExifFormatterExposureTime.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FILE_SOURCE.getId(),
                ExifFormatterFileSource.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FLASH.getId(),
                ExifFormatterFlash.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FOCAL_LENGTH.getId(),
                ExifFormatterFocalLength.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId(),
                ExifFormatterFocalLengthIn35mm.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.F_NUMBER.getId(),
                ExifFormatterFnumber.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_DATE_STAMP.getId(),
                ExifFormatterGpsDateStamp.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_SATELLITES.getId(),
                ExifFormatterGpsSatellites.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_TIME_STAMP.getId(),
                ExifFormatterGpsTimeStamp.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_VERSION_ID.getId(),
                ExifFormatterGpsVersionId.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.IMAGE_UNIQUE_ID.getId(),
                ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.ISO_SPEED_RATINGS.getId(),
                ExifFormatterIsoSpeedRatings.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.METERING_MODE.getId(),
                ExifFormatterMeteringMode.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SATURATION.getId(),
                ExifFormatterSaturation.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SHARPNESS.getId(),
                ExifFormatterSharpness.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.USER_COMMENT.getId(),
                ExifFormatterUserComment.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.WHITE_BALANCE.getId(),
                ExifFormatterWhiteBalance.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.ARTIST.getId(),
                ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.IMAGE_DESCRIPTION.getId(),
                ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.MAKE.getId(),
                ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.MODEL.getId(),
                ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SOFTWARE.getId(),
                ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SPECTRAL_SENSITIVITY.getId(),
                ExifFormatterAscii.INSTANCE);
    }

    /**
     * Returns a formatter for a specific EXIF tag.
     *
     * @param  tagId ID of the exif tag
     * @return formatter or null if no formatter exists for that tag ID
     */
    public static ExifFormatter get(int tagId) {
        return FORMATTER_OF_TAG_ID.get(tagId);
    }

    private ExifFormatterFactory() {
    }
}
