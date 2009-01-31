package de.elmar_baumann.imv.image.metadata.exif;

/**
 * Exif-Tags.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum ExifTag {

    IMAGE_WIDTH(256),
    IMAGE_LENGTH(257),
    BITS_PER_SAMPLE(258),
    MAKE(271),
    MODEL(272),
    SOFTWARE(305),
    DATE_TIME(306),
    EXPOSURE_TIME(33434),
    F_NUMBER(33437),
    EXPOSURE_PROGRAM(34850),
    ISO_SPEED_RATINGS(34855),
    DATE_TIME_ORIGINAL(36867),
    DATE_TIME_DIGITIZED(36868),
    METERING_MODE(37383),
    FLASH(37385),
    FOCAL_LENGTH(37386),
    USER_COMMENT(37510),
    FILE_SOURCE(41728),
    EXPOSURE_MODE(41986),
    WHITE_BALANCE(41987),
    FOCAL_LENGTH_IN_35_MM_FILM(41989),
    CONTRAST(41992),
    SATURATION(41993),
    SHARPNESS(41994),
    SUBJECT_DISTANCE_RANGE(41996),;
    private final int tagId;

    /**
     * Liefert die Tag-ID.
     * 
     * @return Tag-ID
     */
    public int getId() {
        return tagId;
    }

    /**
     * Liefert einen Tag anhand einer ID.
     * 
     * @param  id ID
     * @return Tag oder null bei ungültiger ID
     */
    public static ExifTag getTag(int id) {
        for (ExifTag tag : ExifTag.values()) {
            if (tag.tagId == id) {
                return tag;
            }
        }
        return null;
    }

    private ExifTag(int tagNumber) {
        this.tagId = tagNumber;
    }
}
