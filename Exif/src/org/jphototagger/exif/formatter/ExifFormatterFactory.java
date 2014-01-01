package org.jphototagger.exif.formatter;

import java.util.HashMap;
import java.util.Map;
import org.jphototagger.exif.ExifIfd;
import org.jphototagger.exif.ExifTag;

/**
 * Creates instances of {@code ExifFormatter}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFactory {

    private static final Map<Integer, ExifFormatter> FORMATTER_OF_EXIF_IFD_TAG_ID = new HashMap<>();
    private static final Map<Integer, ExifFormatter> FORMATTER_OF_EXIF_GPS_IFD_TAG_ID = new HashMap<>();

    static {

        // Ordered alphabetically for faster check
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.CONTRAST.getTagId(), ExifFormatterContrast.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.COPYRIGHT.getTagId(), ExifFormatterCopyright.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.DATE_TIME.getTagId(), ExifFormatterDateTime.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.DATE_TIME_ORIGINAL.getTagId(), ExifFormatterDateTimeOriginal.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.DATE_TIME_DIGITIZED.getTagId(), ExifFormatterDateTimeDigitized.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.EXPOSURE_PROGRAM.getTagId(), ExifFormatterExposureProgram.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.EXPOSURE_TIME.getTagId(), ExifFormatterExposureTime.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.FILE_SOURCE.getTagId(), ExifFormatterFileSource.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.FLASH.getTagId(), ExifFormatterFlash.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.FOCAL_LENGTH.getTagId(), ExifFormatterFocalLength.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.FOCAL_LENGTH_IN_35_MM_FILM.getTagId(), ExifFormatterFocalLengthIn35mm.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.F_NUMBER.getTagId(), ExifFormatterFnumber.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.IMAGE_UNIQUE_ID.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.ISO_SPEED_RATINGS.getTagId(), ExifFormatterIsoSpeedRatings.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.METERING_MODE.getTagId(), ExifFormatterMeteringMode.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.SATURATION.getTagId(), ExifFormatterSaturation.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.SHARPNESS.getTagId(), ExifFormatterSharpness.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.USER_COMMENT.getTagId(), ExifFormatterUserComment.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.WHITE_BALANCE.getTagId(), ExifFormatterWhiteBalance.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.ARTIST.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.IMAGE_DESCRIPTION.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.MAKE.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.MODEL.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.SOFTWARE.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Properties.SPECTRAL_SENSITIVITY.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Properties.GPS_DATE_STAMP.getTagId(), ExifFormatterGpsDateStamp.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Properties.GPS_SATELLITES.getTagId(), ExifFormatterGpsSatellites.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Properties.GPS_TIME_STAMP.getTagId(), ExifFormatterGpsTimeStamp.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Properties.GPS_VERSION_ID.getTagId(), ExifFormatterGpsVersionId.INSTANCE);
    }

    /**
     * Returns a formatter for a specific EXIF tag.
     *
     * @param exifTag tag to format
     * @return        formatter for that tag or null if no formatter exists for
     *                that tag
     */
    public static ExifFormatter get(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        ExifIfd ifdType = exifTag.getIfd();
        int tagId = exifTag.getTagId();

        switch (ifdType) {
            case EXIF:
                return FORMATTER_OF_EXIF_IFD_TAG_ID.get(tagId);

            case GPS:
                return FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.get(tagId);

            default:
                return null;
        }
    }

    private ExifFormatterFactory() {
    }
}
