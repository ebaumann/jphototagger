package org.jphototagger.exif;

import com.imagero.reader.tiff.IFDEntry;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jphototagger.exif.datatype.ExifValueType;
import org.jphototagger.lib.xml.bind.Base64ByteArrayXmlAdapter;
import org.jphototagger.lib.xml.bind.Base64ByteStringXmlAdapter;

/**
 * @author Elmar Baumann
 */
@XmlRootElement(name = "exiftag")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExifTag {

    private static final int ANY_VALUE_COUNT = -1;

    /**
     * Human readable tag IDs (rather than the integer IDs of the EXIF standard).
     */
    public enum Properties {

        // Ordered by tag ID
        UNKNOWN(Integer.MIN_VALUE, ExifValueType.UNDEFINED, 0, ExifIfd.UNDEFINED),
        GPS_VERSION_ID(0, ExifValueType.BYTE, 4, ExifIfd.GPS),
        GPS_LATITUDE_REF(1, ExifValueType.ASCII, 2, ExifIfd.GPS),
        GPS_LATITUDE(2, ExifValueType.RATIONAL, 3, ExifIfd.GPS),
        GPS_LONGITUDE_REF(3, ExifValueType.ASCII, 2, ExifIfd.GPS),
        GPS_LONGITUDE(4, ExifValueType.RATIONAL, 3, ExifIfd.GPS),
        GPS_ALTITUDE_REF(5, ExifValueType.BYTE, 1, ExifIfd.GPS),
        GPS_ALTITUDE(6, ExifValueType.RATIONAL, 1, ExifIfd.GPS),
        GPS_TIME_STAMP(7, ExifValueType.RATIONAL, 3, ExifIfd.GPS),
        GPS_SATELLITES(8, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.GPS),
        GPS_DATE_STAMP(29, ExifValueType.ASCII, 11, ExifIfd.GPS),
        IMAGE_WIDTH(256, ExifValueType.SHORT_OR_LONG, 1, ExifIfd.EXIF),
        IMAGE_LENGTH(257, ExifValueType.SHORT_OR_LONG, 1, ExifIfd.EXIF),
        BITS_PER_SAMPLE(258, ExifValueType.SHORT, 3, ExifIfd.EXIF),
        IMAGE_DESCRIPTION(270, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        MAKE(271, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        MODEL(272, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        SOFTWARE(305, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        DATE_TIME(306, ExifValueType.ASCII, 20, ExifIfd.EXIF),
        ARTIST(315, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        COPYRIGHT(33432, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        EXPOSURE_TIME(33434, ExifValueType.RATIONAL, 1, ExifIfd.EXIF),
        F_NUMBER(33437, ExifValueType.RATIONAL, 1, ExifIfd.EXIF),
        EXPOSURE_PROGRAM(34850, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        SPECTRAL_SENSITIVITY(34852, ExifValueType.ASCII, ANY_VALUE_COUNT, ExifIfd.EXIF),
        ISO_SPEED_RATINGS(34855, ExifValueType.SHORT, ANY_VALUE_COUNT, ExifIfd.EXIF),
        DATE_TIME_ORIGINAL(36867, ExifValueType.ASCII, 20, ExifIfd.EXIF),
        DATE_TIME_DIGITIZED(36868, ExifValueType.ASCII, 20, ExifIfd.EXIF),
        MAKER_NOTE(37500, ExifValueType.UNDEFINED, ANY_VALUE_COUNT, ExifIfd.MAKER_NOTE),
        METERING_MODE(37383, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        FLASH(37385, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        FOCAL_LENGTH(37386, ExifValueType.RATIONAL, 1, ExifIfd.EXIF),
        USER_COMMENT(37510, ExifValueType.UNDEFINED, ANY_VALUE_COUNT, ExifIfd.EXIF),
        FILE_SOURCE(41728, ExifValueType.UNDEFINED, 1, ExifIfd.EXIF),
        EXPOSURE_MODE(41986, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        WHITE_BALANCE(41987, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        FOCAL_LENGTH_IN_35_MM_FILM(41989, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        CONTRAST(41992, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        SATURATION(41993, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        SHARPNESS(41994, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        SUBJECT_DISTANCE_RANGE(41996, ExifValueType.SHORT, 1, ExifIfd.EXIF),
        IMAGE_UNIQUE_ID(42016, ExifValueType.ASCII, 33, ExifIfd.EXIF),

        /**
         * Maker notes that shall be displayed. All maker notes equals to or greater than shall be displayed.
         */
        MAKER_NOTE_LENS(3750010, ExifValueType.UNDEFINED, ANY_VALUE_COUNT, ExifIfd.UNDEFINED),
        MAKER_NOTE_CANON_START(3751000, ExifValueType.UNDEFINED, ANY_VALUE_COUNT, ExifIfd.UNDEFINED),
        MAKER_NOTE_NIKON_START(3752000, ExifValueType.UNDEFINED, ANY_VALUE_COUNT, ExifIfd.UNDEFINED),
        ;

        /**
         * Integer tag ID as specified in the EXIF standard
         */
        private final int tagId;
        private final ExifValueType valueType;
        private final int valueCount;
        private final ExifIfd ifd;

        @XmlTransient
        private static final Set<Properties> GPS_TAGS = EnumSet.of(Properties.GPS_ALTITUDE,
                Properties.GPS_ALTITUDE_REF,
                Properties.GPS_DATE_STAMP,
                Properties.GPS_LATITUDE,
                Properties.GPS_LATITUDE_REF,
                Properties.GPS_LONGITUDE,
                Properties.GPS_LONGITUDE_REF,
                Properties.GPS_SATELLITES,
                Properties.GPS_TIME_STAMP,
                Properties.GPS_VERSION_ID);

        private Properties(int value, ExifValueType valueType, int valueCount, ExifIfd ifd) {
            this.tagId = value;
            this.valueType = valueType;
            this.valueCount = valueCount;
            this.ifd = ifd;
        }

        /**
         * @return unique number to identify the EXIF field
         */
        public int getTagId() {
            return tagId;
        }

        /**
         * Returns an tag id with a specific integer getTagId.
         *
         * @param  anInt integer equals to a tag ID
         * @return       Properties or {@code Properties#UNKNOWN} if no tag has such {@link #getTagId()}
         */
        public static Properties parseInt(int anInt) {
            for (Properties id : Properties.values()) {
                if (id.tagId == anInt) {
                    return id;
                }
            }
            return UNKNOWN;
        }

        public boolean isGpsTag() {
            return GPS_TAGS.contains(this);
        }

        public boolean isMakerNoteTag() {
            return MAKER_NOTE.equals(this);
        }

        public ExifValueType getValueType() {
            return valueType;
        }

        /**
         * @return number of values, <em>not</em> the sum of the bytes. The bytes are the value count
         * multiplied with the bytes of the value type, e.g. if the count is 1 and the value type
         * is {@link ExifValueType#SHORT} (16 bits), the byte count is 2 (32 bits)
         */
        public int getValueCount() {
            return valueCount;
        }

        public ExifIfd getIfd() {
            return ifd;
        }

        public boolean isAnyValueCount() {
            return valueCount < 0;
        }
    }
    /**
     * IFD where the tag comes from
     */
    private final ExifIfd ifd;
    /**
     * Tag identifier, bytes 0 - 1 in the IFD entry
     */
    private final int tagId;
    /**
     * Data type identifier, bytes 2 - 3 in the IFD entry
     */
    private final int intValueType;
    /**
     * Value count, bytes 4 - 7 in the IFD entry
     *
     * (!= byte count, 1 SHORT == getTagId count of 1 even it requires 2 bytes of
     * storage)
     */
    private final int valueCount;
    /**
     * Offset in bytes from the TIFF header to the getTagId, bytes 8 - 11 in the
     * IFD entry. If the getTagId fits in 4 bytes the getTagId itself, starting from
     * left.
     */
    private final long valueOffset;

    @XmlJavaTypeAdapter(Base64ByteArrayXmlAdapter.class)
    private byte[] rawValue;

    // The string getTagId may contain "\000" not allowed in XML
    @XmlJavaTypeAdapter(Base64ByteStringXmlAdapter.class)
    private final String stringValue;

    private final String name;

    private final int intByteOrder;

    /**
     * Only for JAXB!
     */
    public ExifTag() {
        valueOffset = -1;
        rawValue = new byte[]{};
        stringValue = "";
        name = "";
        intByteOrder = -1;
        valueCount = -1;
        tagId = -1;
        intValueType = -1;
        ifd = ExifIfd.UNDEFINED;
    }

    public ExifTag(IFDEntry entry, ExifIfd ifd) {
        if (entry == null) {
            throw new NullPointerException("ifd == null");
        }
        if (ifd == null) {
            throw new NullPointerException("ifdType == null");
        }
        tagId = entry.getTag();
        valueCount = entry.getCount();
        valueOffset = entry.getValueOffset();
        intValueType = entry.getType();
        name = entry.getEntryMeta().getName();
        intByteOrder = entry.parent.getByteOrder();
        rawValue = deepCopyRawValueOfIfdEntry(entry);
        stringValue = entry.toString();
        this.ifd = ifd;
    }

    /**
     * @param tagId
     * @param intValueType
     * @param valueCount
     * @param valueOffset
     * @param rawValue    can be null
     * @param stringValue can be null
     * @param intByteOrder
     * @param name        can be null
     * @param ifd
     */
    public ExifTag(
            int tagId,
            int intValueType,
            int valueCount,
            long valueOffset,
            byte[] rawValue,
            String stringValue,
            int intByteOrder,
            String name,
            ExifIfd ifd) {
        this.tagId = tagId;
        this.intValueType = intValueType;
        this.valueCount = valueCount;
        this.valueOffset = valueOffset;
        this.rawValue = rawValue == null
                ? new byte[0]
                : Arrays.copyOf(rawValue, rawValue.length);
        this.stringValue = stringValue;
        this.intByteOrder = intByteOrder;
        this.name = name;
        this.ifd = ifd;
    }

    public ByteOrder convertByteOrderIdToByteOrder() {
        // 0x4949 (18761) == little endian, 0x4D4D (19789) == big endian
        return (intByteOrder == 18761)
                ? ByteOrder.LITTLE_ENDIAN
                : ByteOrder.BIG_ENDIAN;
    }

    public int getByteOrderId() {
        return intByteOrder;
    }

    public String getName() {
        return name;
    }

    public byte[] getRawValue() {
        return Arrays.copyOf(rawValue, rawValue.length);
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getTagId() {
        return tagId;
    }

    public ExifIfd getIfd() {
        return ifd;
    }

    public long getValueOffset() {
        return valueOffset;
    }

    public int getValueCount() {
        return valueCount;
    }

    public int getIntValueType() {
        return intValueType;
    }

    public ExifValueType parseValueType() {
        return ExifValueType.parseInt(intValueType);
    }

    public Properties parseProperties() {
        return Properties.parseInt(tagId);
    }

    private byte[] deepCopyRawValueOfIfdEntry(IFDEntry entry) {
        try {
            return Arrays.copyOf(entry.getRawValue(), entry.getRawValue().length);
        } catch (Throwable t){
            Logger.getLogger(ExifTag.class.getName()).log(Level.SEVERE, null, t);
        }

        return null;
    }

    @Override
    public String toString() {
        return name == null
                ? parseValueType().name()
                : name;
    }

    /**
     *
     * @param  obj
     * @return     true, if IFD type {@code #getIfd()} and tag ID {@code #getTagId()} both equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ExifTag)) {
            return false;
        }
        ExifTag other = (ExifTag) obj;
        return ifd == other.ifd && tagId == other.tagId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.ifd != null
                ? this.ifd.hashCode()
                : 0);
        hash = 13 * hash + this.tagId;
        return hash;
    }
}
