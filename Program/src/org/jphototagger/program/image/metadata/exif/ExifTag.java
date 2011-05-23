package org.jphototagger.program.image.metadata.exif;

import com.imagero.reader.tiff.IFDEntry;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.metadata.exif.datatype.ExifDataType;
import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jphototagger.lib.xml.bind.Base64ByteArrayXmlAdapter;
import org.jphototagger.lib.xml.bind.Base64ByteStringXmlAdapter;

/**
 *
 * @author Elmar Baumann
 */
@XmlRootElement(name = "exiftag")
@XmlAccessorType(XmlAccessType.FIELD)
public final class ExifTag {

    /**
     * Human understandable tag IDs (rather than the integer IDs).
     * <p>
     * Maps the integer tag id.
     */
    public enum Id {

        // Ordered by tag ID
        UNKNOWN(Integer.MIN_VALUE),
        GPS_VERSION_ID(0),
        GPS_LATITUDE_REF(1),
        GPS_LATITUDE(2),
        GPS_LONGITUDE_REF(3),
        GPS_LONGITUDE(4),
        GPS_ALTITUDE_REF(5),
        GPS_ALTITUDE(6),
        GPS_TIME_STAMP(7),
        GPS_SATELLITES(8),
        GPS_DATE_STAMP(29),
        IMAGE_WIDTH(256),
        IMAGE_LENGTH(257),
        BITS_PER_SAMPLE(258),
        IMAGE_DESCRIPTION(270),
        MAKE(271),
        MODEL(272),
        SOFTWARE(305),
        DATE_TIME(306),
        ARTIST(315),
        COPYRIGHT(33432),
        EXPOSURE_TIME(33434),
        F_NUMBER(33437),

        EXPOSURE_PROGRAM(34850),
        SPECTRAL_SENSITIVITY(34852),
        ISO_SPEED_RATINGS(34855),
        DATE_TIME_ORIGINAL(36867),

        DATE_TIME_DIGITIZED(36868),
        MAKER_NOTE(37500),


        /**
         * Maker note that shall be displayed. Alle maker notes equals to or
         * grater than this getTagId will be displayed.
         */
        MAKER_NOTE_LENS(3750010),
        MAKER_NOTE_CANON_START(3751000),
        MAKER_NOTE_NIKON_START(3752000),

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

        SUBJECT_DISTANCE_RANGE(41996),
        IMAGE_UNIQUE_ID(42016),
        ;
        
        /**
         * Integer getTagId of tag ID as specified in the EXIF standard
         */
        private final int tagId;
    
        @XmlTransient
        private static final Set<Id> GPS_IDS = EnumSet.of(Id.GPS_ALTITUDE,
                                                          Id.GPS_ALTITUDE_REF,
                                                          Id.GPS_DATE_STAMP,
                                                          Id.GPS_LATITUDE,
                                                          Id.GPS_LATITUDE_REF,
                                                          Id.GPS_LONGITUDE,
                                                          Id.GPS_LONGITUDE_REF,
                                                          Id.GPS_SATELLITES,
                                                          Id.GPS_TIME_STAMP,
                                                          Id.GPS_VERSION_ID);

        /**
         * Returns the integer getTagId of this tag ID.
         *
         * @return getTagId
         */
        public int getTagId() {
            return tagId;
        }

        /**
         * Returns an tag id with a specific integer getTagId.
         *
         * @param  tagId tag ID
         * @return       Id or {@link Id#UNKNOWN} if no tag has such getTagId
         */
        public static Id convertTagIdToId(int tagId) {
            for (Id id : Id.values()) {
                if (id.tagId == tagId) {
                    return id;
                }
            }

            return UNKNOWN;
        }

        private Id(int value) {
            this.tagId = value;
        }
        
        public boolean isGpsId() {
            return GPS_IDS.contains(this);
        }
        
        public boolean isMakerNoteId() {
            return MAKER_NOTE.equals(this);
        }
    }

    @XmlTransient
    private static final Map<Integer, ExifDataType> DATA_TYPE_OF_TAG_ID = new HashMap<Integer, ExifDataType>();

    static {
        for (ExifDataType type : ExifDataType.values()) {
            DATA_TYPE_OF_TAG_ID.put(type.getValue(), type);
        }
    }

    /**
     * IFD where the tag comes from
     */
    private final IfdType ifdType;

    /**
     * Tag identifier, bytes 0 - 1 in the IFD entry
     */
    private final int tagId;

    /**
     * Data type identifier, bytes 2 - 3 in the IFD entry
     */
    private final int dataTypeId;

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

    private final int byteOrderId;

    /**
     * Only for JAXB!
     */
    public ExifTag() {
        valueOffset = -1;
        rawValue = new byte[]{};
        stringValue = "";
        name = "";
        byteOrderId = -1;
        valueCount = -1;
        tagId = -1;
        dataTypeId = -1;
        ifdType = IfdType.UNDEFINED;
    }

    public ExifTag(IFDEntry entry, ExifMetadata.IfdType ifdType) {
        if (entry == null) {
            throw new NullPointerException("entry == null");
        }

        if (ifdType == null) {
            throw new NullPointerException("ifdType == null");
        }

        tagId = entry.getTag();
        valueCount = entry.getCount();
        valueOffset = entry.getValueOffset();
        dataTypeId = entry.getType();
        name = entry.getEntryMeta().getName();
        byteOrderId = entry.parent.getByteOrder();
        rawValue = deepCopyRawValueOfIfdEntry(entry);
        stringValue = entry.toString();
        this.ifdType = ifdType;
    }

    /**
     *
     * @param tagId
     * @param dataTypeId
     * @param valueCount
     * @param valueOffset
     * @param rawValue    can be null
     * @param stringValue can be null
     * @param byteOrderId
     * @param name        can be null
     * @param ifdType
     */
    public ExifTag(int tagId, int dataTypeId, int valueCount, long valueOffset, byte[] rawValue, String stringValue,
                   int byteOrderId, String name, ExifMetadata.IfdType ifdType) {
        this.tagId = tagId;
        this.dataTypeId = dataTypeId;
        this.valueCount = valueCount;
        this.valueOffset = valueOffset;
        this.rawValue = (rawValue == null)
                        ? null
                        : Arrays.copyOf(rawValue, rawValue.length);
        this.stringValue = stringValue;
        this.name = name;
        this.byteOrderId = byteOrderId;
        this.ifdType = ifdType;
    }

    public ByteOrder convertByteOrderIdToByteOrder() {

        // 0x4949 (18761) == little endian, 0x4D4D (19789) == big endian
        return (byteOrderId == 18761)
               ? ByteOrder.LITTLE_ENDIAN
               : ByteOrder.BIG_ENDIAN;
    }

    public int getByteOrderId() {
        return byteOrderId;
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

    public Id convertTagIdToEnumId() {
        return Id.convertTagIdToId(tagId);
    }

    public IfdType getIfdType() {
        return ifdType;
    }
    
    public long getValueOffset() {
        return valueOffset;
    }

    public int getValueCount() {
        return valueCount;
    }

    public ExifDataType convertDataTypeIdToExifDataType() {
        return convertDataTypeIdToExifDataType(dataTypeId);
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    private byte[] deepCopyRawValueOfIfdEntry(IFDEntry entry) {
        try {
            return Arrays.copyOf(entry.getRawValue(), entry.getRawValue().length);
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }

        return null;
    }

    private ExifDataType convertDataTypeIdToExifDataType(int tagId) {
        ExifDataType t = DATA_TYPE_OF_TAG_ID.get(tagId);

        if (t == null) {
            return ExifDataType.UNDEFINED;
        }

        return t;
    }
    
    @Override
    public String toString() {
        return (name == null)
               ? " Undefined "
               : name;
    }

    /**
     *
     * @param  obj
     * @return     true, if IFD type {@link #getIfdType()} and tag ID {@link #getTagId()} both equals
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

        return ifdType.equals(other.ifdType) && (tagId == other.tagId);
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 13 * hash + ((this.ifdType != null)
                            ? this.ifdType.hashCode()
                            : 0);
        hash = 13 * hash + this.tagId;

        return hash;
    }

    public String toInfoString() {
        String pattern = "EXIF Tag ["
                + "ID: {0}"
                + ", Name: {1}"
                + ", Number of values: {2}"
                + ", Value offset: {3}"
                + ", Data type: {4}"
                + ", Raw value byte count: {5}"
                + ", Byte order: {6}"
                + ", String Value: {7}"
                + ", IFD Type: {8}"
                + "]";

        return MessageFormat.format(pattern,
                tagId,
                (name == null) ? " Undefined " : name,
                valueCount,
                valueOffset,
                convertDataTypeIdToExifDataType(),
                (rawValue == null) ? 0 : rawValue.length,
                convertByteOrderIdToByteOrder(),
                (stringValue == null) ? "" : stringValue,
                ifdType);
    }
}
