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

import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifCount;
import de.elmar_baumann.jpt.image.metadata.exif.datatype.ExifType;

/**
 * Exif-Tags.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum ExifTag {

    // Ordered by tag ID
    GPS_VERSION_ID(0, ExifType.BYTE, ExifCount.NUMBER_4),
    GPS_LATITUDE_REF(1, ExifType.ASCII, ExifCount.NUMBER_2),
    GPS_LATITUDE(2, ExifType.RATIONAL, ExifCount.NUMBER_3),
    GPS_LONGITUDE_REF(3, ExifType.ASCII, ExifCount.NUMBER_2),
    GPS_LONGITUDE(4, ExifType.RATIONAL, ExifCount.NUMBER_3),
    GPS_ALTITUDE_REF(5, ExifType.BYTE, ExifCount.NUMBER_1),
    GPS_ALTITUDE(6, ExifType.RATIONAL, ExifCount.NUMBER_1),
    GPS_TIME_STAMP(7, ExifType.RATIONAL, ExifCount.NUMBER_3),
    GPS_SATELLITES(8, ExifType.ASCII, ExifCount.ANY),
    GPS_DATE_STAMP(29, ExifType.ASCII, ExifCount.NUMBER_11),
    IMAGE_WIDTH(256, ExifType.SHORT_OR_LONG, ExifCount.NUMBER_1),
    IMAGE_LENGTH(257, ExifType.SHORT_OR_LONG, ExifCount.NUMBER_1),
    BITS_PER_SAMPLE(258, ExifType.SHORT, ExifCount.NUMBER_3),
    IMAGE_DESCRIPTION(270, ExifType.ASCII, ExifCount.ANY),
    MAKE(271, ExifType.ASCII, ExifCount.ANY),
    MODEL(272, ExifType.ASCII, ExifCount.ANY),
    SOFTWARE(305, ExifType.ASCII, ExifCount.ANY),
    DATE_TIME(306, ExifType.ASCII, ExifCount.NUMBER_20),
    ARTIST(315, ExifType.ASCII, ExifCount.ANY),
    COPYRIGHT(33432, ExifType.ASCII, ExifCount.ANY),
    EXPOSURE_TIME(33434, ExifType.RATIONAL, ExifCount.NUMBER_1),
    F_NUMBER(33437, ExifType.RATIONAL, ExifCount.NUMBER_1),
    EXPOSURE_PROGRAM(34850, ExifType.SHORT, ExifCount.NUMBER_1),
    SPECTRAL_SENSITIVITY(34852, ExifType.ASCII, ExifCount.ANY),
    ISO_SPEED_RATINGS(34855, ExifType.SHORT, ExifCount.ANY),
    DATE_TIME_ORIGINAL(36867, ExifType.ASCII, ExifCount.NUMBER_20),
    DATE_TIME_DIGITIZED(36868, ExifType.ASCII, ExifCount.NUMBER_20),
    METERING_MODE(37383, ExifType.SHORT, ExifCount.NUMBER_1),
    FLASH(37385, ExifType.SHORT, ExifCount.NUMBER_1),
    FOCAL_LENGTH(37386, ExifType.RATIONAL, ExifCount.NUMBER_1),
    USER_COMMENT(37510, ExifType.UNDEFINED, ExifCount.ANY),
    FILE_SOURCE(41728, ExifType.UNDEFINED, ExifCount.NUMBER_1),
    EXPOSURE_MODE(41986, ExifType.SHORT, ExifCount.NUMBER_1),
    WHITE_BALANCE(41987, ExifType.SHORT, ExifCount.NUMBER_1),
    FOCAL_LENGTH_IN_35_MM_FILM(41989, ExifType.SHORT, ExifCount.NUMBER_1),
    CONTRAST(41992, ExifType.SHORT, ExifCount.NUMBER_1),
    SATURATION(41993, ExifType.SHORT, ExifCount.NUMBER_1),
    SHARPNESS(41994, ExifType.SHORT, ExifCount.NUMBER_1),
    SUBJECT_DISTANCE_RANGE(41996, ExifType.SHORT, ExifCount.NUMBER_1),
    IMAGE_UNIQUE_ID(42016, ExifType.ASCII, ExifCount.NUMBER_33),;
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

    private ExifTag(int tagNumber, ExifType type, ExifCount count) {
        this.tagId = tagNumber;
        this.type = type;
        this.count = count;
    }
}
