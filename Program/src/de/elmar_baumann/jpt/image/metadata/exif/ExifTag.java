/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.image.metadata.exif;

import com.imagero.reader.tiff.IFDEntry;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifDataType;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-15
 */
public final class ExifTag implements Comparable<ExifTag> {

    /**
     * Human understandable tag IDs (rather than the integer IDs).
     * <p>
     * Maps the integer tag id.
     */
    public enum Id {

        // Ordered by tag ID
        GPS_VERSION_ID            (    0),
        GPS_LATITUDE_REF          (    1),
        GPS_LATITUDE              (    2),
        GPS_LONGITUDE_REF         (    3),
        GPS_LONGITUDE             (    4),
        GPS_ALTITUDE_REF          (    5),
        GPS_ALTITUDE              (    6),
        GPS_TIME_STAMP            (    7),
        GPS_SATELLITES            (    8),
        GPS_DATE_STAMP            (   29),
        IMAGE_WIDTH               (  256),
        IMAGE_LENGTH              (  257),
        BITS_PER_SAMPLE           (  258),
        IMAGE_DESCRIPTION         (  270),
        MAKE                      (  271),
        MODEL                     (  272),
        SOFTWARE                  (  305),
        DATE_TIME                 (  306),
        ARTIST                    (  315),
        COPYRIGHT                 (33432),
        EXPOSURE_TIME             (33434),
        F_NUMBER                  (33437),
        EXPOSURE_PROGRAM          (34850),
        SPECTRAL_SENSITIVITY      (34852),
        ISO_SPEED_RATINGS         (34855),
        DATE_TIME_ORIGINAL        (36867),
        DATE_TIME_DIGITIZED       (36868),
        MAKER_NOTE                (37500),
        /**
         * Maker note that shall be displayed. Alle maker notes equals to or
         * grater than this value will be displayed.
         */
        DISPLAYABLE_MAKER_NOTE    (3750000),
        METERING_MODE             (37383),
        FLASH                     (37385),
        FOCAL_LENGTH              (37386),
        USER_COMMENT              (37510),
        FILE_SOURCE               (41728),
        EXPOSURE_MODE             (41986),
        WHITE_BALANCE             (41987),
        FOCAL_LENGTH_IN_35_MM_FILM(41989),
        CONTRAST                  (41992),
        SATURATION                (41993),
        SHARPNESS                 (41994),
        SUBJECT_DISTANCE_RANGE    (41996),
        IMAGE_UNIQUE_ID           (42016),
        ;

        /**
         * Integer value of tag ID as specified in the EXIF standard
         */
        private final int value;

        /**
         * Returns the integer value of this tag ID.
         *
         * @return value
         */
        public int value() {
            return value;
        }

        /**
         * Returns an tag id with a specific integer value.
         *
         * @param  value value
         * @return       Id or null if no tag has such value
         */
        public static Id fromValue(int value) {
            for (Id id : Id.values()) {
                if (id.value == value) {
                    return id;
                }
            }
            return null;
        }

        private Id(int value) {
            this.value = value;
        }
    }

    private static final Map<Integer, ExifDataType> DATA_TYPE_OF_TAG_ID = new HashMap<Integer, ExifDataType>();

    static {
        for (ExifDataType type : ExifDataType.values()) {
            DATA_TYPE_OF_TAG_ID.put(type.getValue(), type);
        }
    }

    private final int    idValue;
    private final int    valueCount;
    private final long   fileOffset;
    private final int    dataTypeId;
    private       byte[] rawValue;
    private final String stringValue;
    private final String name;
    private final int    byteOrderId;

    public ExifTag(IFDEntry entry) {
            idValue     = entry.getTag();
            valueCount  = entry.getCount();
            fileOffset  = entry.getValueOffset();
            dataTypeId  = entry.getType();
            name        = entry.getEntryMeta().getName();
            byteOrderId = entry.parent.getByteOrder();
            rawValue    = rawValueDeepCopy(entry);
            stringValue = entry.toString();
    }

    public ExifTag(
            int    tagId,
            int    dataTypeId,
            int    valueCount,
            long   fileOffset,
            byte[] rawValue,
            String stringValue,
            int    byteOrderId,
            String name
            ) {
        this.idValue     = tagId;
        this.dataTypeId  = dataTypeId;
        this.valueCount  = valueCount;
        this.fileOffset  = fileOffset;
        this.rawValue    = rawValue;
        this.stringValue = stringValue;
        this.name        = name;
        this.byteOrderId = byteOrderId;
    }

    public ByteOrder byteOrder() {
        return byteOrderId == 18761
                ? ByteOrder.LITTLE_ENDIAN
                : ByteOrder.BIG_ENDIAN;
    }

    public int byteOrderId() {
        return byteOrderId;
    }

    public String name() {
        return name;
    }

    public byte[] rawValue() {
        return Arrays.copyOf(rawValue, rawValue.length);
    }

    public String stringValue() {
        return stringValue;
    }

    public int idValue() {
        return idValue;
    }

    public Id id() {
        return Id.fromValue(idValue);
    }

    public long fileOffset() {
        return fileOffset;
    }

    public int valueCount() {
        return valueCount;
    }

    public ExifDataType dataType() {
        return dataTypeOfTagId(dataTypeId);
    }

    public int dataTypeId() {
        return dataTypeId;
    }

    private byte[] rawValueDeepCopy(IFDEntry entry) {
        try {
            return Arrays.copyOf(entry.getRawValue(), entry.getRawValue().length);
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
        return null;
    }

    private ExifDataType dataTypeOfTagId(int tagId) {

        ExifDataType t = DATA_TYPE_OF_TAG_ID.get(tagId);

        if (t == null) return ExifDataType.UNDEFINED;

        return t;
    }

    /**
     * Compares the ID values.
     *
     * @param  otherTag other tag
     * @return          see interface doc
     */
    @Override
    public int compareTo(ExifTag otherTag) {
        return idValue - otherTag.idValue;
    }

    @Override
    public String toString() {
        return "EXIF Tag [" +
                                    "ID: " + idValue +
                                ", Name: " + (name   == null ? " Undefined " : name) +
                    ", Number of values: " + valueCount +
                         ", File offset: " + fileOffset +
                           ", Data type: " + dataType().toString() +
                ", Raw value byte count: " + (rawValue == null ? 0 : rawValue.length) +
                          ", Byte order: " + byteOrder().toString() +
                        ", String Value: " + (stringValue == null ? "" : stringValue) +
                "]"
                ;
    }
}
