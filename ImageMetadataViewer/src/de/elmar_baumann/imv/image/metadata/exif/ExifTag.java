package de.elmar_baumann.imv.image.metadata.exif;

/**
 * Exif-Tags.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum ExifTag {

    ImageWidth(256),
    ImageLength(257),
    BitsPerSample(258),
    Make(271),
    Model(272),
    Software(305),
    DateTime(306),
    ExposureTime(33434),
    FNumber(33437),
    ExposureProgram(34850),
    ISOSpeedRatings(34855),
    DateTimeOriginal(36867),
    DateTimeDigitized(36868),
    MeteringMode(37383),
    Flash(37385),
    FocalLength(37386),
    UserComment(37510),
    FileSource(41728),
    ExposureMode(41986),
    WhiteBalance(41987),
    FocalLengthIn35mmFilm(41989),
    Contrast(41992),
    Saturation(41993),
    Sharpness(41994),
    SubjectDistanceRange(41996),;
    private int tagId;

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
