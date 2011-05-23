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
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.CONTRAST.getTagId(), ExifFormatterContrast.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.COPYRIGHT.getTagId(), ExifFormatterCopyright.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.DATE_TIME_ORIGINAL.getTagId(), ExifFormatterDateTime.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.EXPOSURE_PROGRAM.getTagId(), ExifFormatterExposureProgram.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.EXPOSURE_TIME.getTagId(), ExifFormatterExposureTime.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FILE_SOURCE.getTagId(), ExifFormatterFileSource.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FLASH.getTagId(), ExifFormatterFlash.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FOCAL_LENGTH.getTagId(), ExifFormatterFocalLength.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.getTagId(),
                                         ExifFormatterFocalLengthIn35mm.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.F_NUMBER.getTagId(), ExifFormatterFnumber.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.IMAGE_UNIQUE_ID.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.ISO_SPEED_RATINGS.getTagId(), ExifFormatterIsoSpeedRatings.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.METERING_MODE.getTagId(), ExifFormatterMeteringMode.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SATURATION.getTagId(), ExifFormatterSaturation.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SHARPNESS.getTagId(), ExifFormatterSharpness.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.USER_COMMENT.getTagId(), ExifFormatterUserComment.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.WHITE_BALANCE.getTagId(), ExifFormatterWhiteBalance.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.ARTIST.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.IMAGE_DESCRIPTION.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.MAKE.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.MODEL.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SOFTWARE.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_IFD_TAG_ID.put(ExifTag.Id.SPECTRAL_SENSITIVITY.getTagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_DATE_STAMP.getTagId(), ExifFormatterGpsDateStamp.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_SATELLITES.getTagId(), ExifFormatterGpsSatellites.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_TIME_STAMP.getTagId(), ExifFormatterGpsTimeStamp.INSTANCE);
        FORMATTER_OF_EXIF_GPS_IFD_TAG_ID.put(ExifTag.Id.GPS_VERSION_ID.getTagId(), ExifFormatterGpsVersionId.INSTANCE);
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

        IfdType ifdType = exifTag.getIfdType();
        int tagId = exifTag.getTagId();

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
