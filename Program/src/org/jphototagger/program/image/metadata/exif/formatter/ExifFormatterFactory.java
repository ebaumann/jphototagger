package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates instances of {@link ExifFormatter}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterFactory {
    private static final Map<Integer, ExifFormatter> FORMATTER_OF_EXIF_IFD_TAG_ID = new HashMap<Integer,
                                                                                        ExifFormatter>();
    private static final Map<Integer, ExifFormatter> FORMATTER_OF_EXIF_GPS_IFD_TAG_ID = new HashMap<Integer,
                                                                                            ExifFormatter>();

    static {

        // Ordered alphabetically for faster check
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.CONTRAST.value(), ExifFormatterContrast.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.COPYRIGHT.value(), ExifFormatterCopyright.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.DATE_TIME_ORIGINAL.value(), ExifFormatterDateTime.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.EXPOSURE_PROGRAM.value(), ExifFormatterExposureProgram.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.EXPOSURE_TIME.value(), ExifFormatterExposureTime.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FILE_SOURCE.value(), ExifFormatterFileSource.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FLASH.value(), ExifFormatterFlash.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FOCAL_LENGTH.value(), ExifFormatterFocalLength.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.value(),
                                         ExifFormatterFocalLengthIn35mm.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.F_NUMBER.value(), ExifFormatterFnumber.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.IMAGE_UNIQUE_ID.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.ISO_SPEED_RATINGS.value(), ExifFormatterIsoSpeedRatings.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.METERING_MODE.value(), ExifFormatterMeteringMode.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SATURATION.value(), ExifFormatterSaturation.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SHARPNESS.value(), ExifFormatterSharpness.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.USER_COMMENT.value(), ExifFormatterUserComment.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.WHITE_BALANCE.value(), ExifFormatterWhiteBalance.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.ARTIST.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.IMAGE_DESCRIPTION.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.MAKE.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.MODEL.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SOFTWARE.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SPECTRAL_SENSITIVITY.value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_DATE_STAMP.value(), ExifFormatterGpsDateStamp.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_SATELLITES.value(), ExifFormatterGpsSatellites.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_TIME_STAMP.value(), ExifFormatterGpsTimeStamp.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_VERSION_ID.value(), ExifFormatterGpsVersionId.INSTANCE);
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

        IfdType ifdType = exifTag.ifdType();
        int tagId = exifTag.idValue();

        switch (ifdType) {
        case EXIF :
            return FORMATTER_OF_EXIF_IFD_TAG_ID.get(tagId);

        case GPS :
            return FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.get(tagId);

        default :
            return null;
        }
    }

    private ExifFormatterFactory() {}
}
