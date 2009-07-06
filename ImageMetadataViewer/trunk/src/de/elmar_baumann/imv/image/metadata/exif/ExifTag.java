package de.elmar_baumann.imv.image.metadata.exif;

import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifCount;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifType;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatter;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterAscii;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterContrast;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterCopyright;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterDateTime;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterExposureProgram;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterExposureTime;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterFileSource;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterFlash;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterFnumber;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterFocalLength;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterFocalLengthIn35mm;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterGpsDateStamp;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterGpsSatellites;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterGpsTimeStamp;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterGpsVersionId;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterIsoSpeedRatings;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterMeteringMode;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterSaturation;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterSharpness;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterUserComment;
import de.elmar_baumann.imv.image.metadata.exif.format.ExifFormatterWhiteBalance;

/**
 * Exif-Tags.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum ExifTag {

    // Ordered by tag ID
    GPS_VERSION_ID(
    0, ExifType.BYTE, ExifCount.NUMBER_4, ExifFormatterGpsVersionId.INSTANCE),
    GPS_LATITUDE_REF(
    1, ExifType.ASCII, ExifCount.NUMBER_2, null),
    GPS_LATITUDE(
    2, ExifType.RATIONAL, ExifCount.NUMBER_3, null),
    GPS_LONGITUDE_REF(
    3, ExifType.ASCII, ExifCount.NUMBER_2, null),
    GPS_LONGITUDE(
    4, ExifType.RATIONAL, ExifCount.NUMBER_3, null),
    GPS_ALTITUDE_REF(
    5, ExifType.BYTE, ExifCount.NUMBER_1, null),
    GPS_ALTITUDE(
    6, ExifType.RATIONAL, ExifCount.NUMBER_1, null),
    GPS_TIME_STAMP(
    7, ExifType.RATIONAL, ExifCount.NUMBER_3, ExifFormatterGpsTimeStamp.INSTANCE),
    GPS_SATELLITES(
    8, ExifType.ASCII, ExifCount.ANY, ExifFormatterGpsSatellites.INSTANCE),
    GPS_DATE_STAMP(
    29, ExifType.ASCII, ExifCount.NUMBER_11, ExifFormatterGpsDateStamp.INSTANCE),
    IMAGE_WIDTH(
    256, ExifType.SHORT_OR_LONG, ExifCount.NUMBER_1, null),
    IMAGE_LENGTH(
    257, ExifType.SHORT_OR_LONG, ExifCount.NUMBER_1, null),
    BITS_PER_SAMPLE(
    258, ExifType.SHORT, ExifCount.NUMBER_3, null),
    IMAGE_DESCRIPTION(
    270, ExifType.ASCII, ExifCount.ANY, ExifFormatterAscii.INSTANCE),
    MAKE(
    271, ExifType.ASCII, ExifCount.ANY, ExifFormatterAscii.INSTANCE),
    MODEL(
    272, ExifType.ASCII, ExifCount.ANY, ExifFormatterAscii.INSTANCE),
    SOFTWARE(
    305, ExifType.ASCII, ExifCount.ANY, ExifFormatterAscii.INSTANCE),
    DATE_TIME(
    306, ExifType.ASCII, ExifCount.NUMBER_20, ExifFormatterDateTime.INSTANCE),
    ARTIST(
    315, ExifType.ASCII, ExifCount.ANY, ExifFormatterAscii.INSTANCE),
    COPYRIGHT(
    33432, ExifType.ASCII, ExifCount.ANY, ExifFormatterCopyright.INSTANCE),
    EXPOSURE_TIME(
    33434, ExifType.RATIONAL, ExifCount.NUMBER_1,
    ExifFormatterExposureTime.INSTANCE),
    F_NUMBER(
    33437, ExifType.RATIONAL, ExifCount.NUMBER_1, ExifFormatterFnumber.INSTANCE),
    EXPOSURE_PROGRAM(
    34850, ExifType.SHORT, ExifCount.NUMBER_1,
    ExifFormatterExposureProgram.INSTANCE),
    SPECTRAL_SENSITIVITY(
    34852, ExifType.ASCII, ExifCount.ANY, ExifFormatterAscii.INSTANCE),
    ISO_SPEED_RATINGS(
    34855, ExifType.SHORT, ExifCount.ANY, ExifFormatterIsoSpeedRatings.INSTANCE),
    DATE_TIME_ORIGINAL(
    36867, ExifType.ASCII, ExifCount.NUMBER_20, ExifFormatterDateTime.INSTANCE),
    DATE_TIME_DIGITIZED(
    36868, ExifType.ASCII, ExifCount.NUMBER_20, ExifFormatterDateTime.INSTANCE),
    METERING_MODE(
    37383, ExifType.SHORT, ExifCount.NUMBER_1,
    ExifFormatterMeteringMode.INSTANCE),
    FLASH(
    37385, ExifType.SHORT, ExifCount.NUMBER_1, ExifFormatterFlash.INSTANCE),
    FOCAL_LENGTH(
    37386, ExifType.RATIONAL, ExifCount.NUMBER_1,
    ExifFormatterFocalLength.INSTANCE),
    USER_COMMENT(
    37510, ExifType.UNDEFINED, ExifCount.ANY, ExifFormatterUserComment.INSTANCE),
    FILE_SOURCE(
    41728, ExifType.UNDEFINED, ExifCount.NUMBER_1,
    ExifFormatterFileSource.INSTANCE),
    EXPOSURE_MODE(
    41986, ExifType.SHORT, ExifCount.NUMBER_1, null),
    WHITE_BALANCE(
    41987, ExifType.SHORT, ExifCount.NUMBER_1,
    ExifFormatterWhiteBalance.INSTANCE),
    FOCAL_LENGTH_IN_35_MM_FILM(
    41989, ExifType.SHORT, ExifCount.NUMBER_1,
    ExifFormatterFocalLengthIn35mm.INSTANCE),
    CONTRAST(
    41992, ExifType.SHORT, ExifCount.NUMBER_1, ExifFormatterContrast.INSTANCE),
    SATURATION(
    41993, ExifType.SHORT, ExifCount.NUMBER_1, ExifFormatterSaturation.INSTANCE),
    SHARPNESS(
    41994, ExifType.SHORT, ExifCount.NUMBER_1, ExifFormatterSharpness.INSTANCE),
    SUBJECT_DISTANCE_RANGE(
    41996, ExifType.SHORT, ExifCount.NUMBER_1, null),
    IMAGE_UNIQUE_ID(
    42016, ExifType.ASCII, ExifCount.NUMBER_33, ExifFormatterAscii.INSTANCE),;
    /**
     * Tag ID as specified in the EXIF standard
     */
    private final int tagId;
    /**
     * Data type of the EXIF tag
     */
    private final ExifType type;
    /**
     * Count of data
     */
    private final ExifCount count;
    /**
     * Formatter of the tag
     */
    private final ExifFormatter formatter;

    /**
     * Returns the Tag ID.
     * 
     * @return tag ID
     */
    public int getId() {
        return tagId;
    }

    /**
     * Returns a tag with an ID.
     * 
     * @param  id ID
     * @return Tag or null if the ID is invalid
     */
    public static ExifTag getTag(int id) {
        for (ExifTag tag : ExifTag.values()) {
            if (tag.tagId == id) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Returns the data type of the EXIF tag.
     *
     * @return data type
     */
    public ExifType getType() {
        return type;
    }

    /**
     * Returns the count of data.
     *
     * @return count
     */
    public ExifCount getCount() {
        return count;
    }

    /**
     * Returns the formatter of an EXIF tag.
     *
     * @return formatter or null if no formatter is defined
     */
    public ExifFormatter getFormatter() {
        return formatter;
    }

    private ExifTag(
            int tagNumber, ExifType type, ExifCount count,
            ExifFormatter formatter) {
        this.tagId = tagNumber;
        this.type = type;
        this.count = count;
        this.formatter = formatter;
    }
}
