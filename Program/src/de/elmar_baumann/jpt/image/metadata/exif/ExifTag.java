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

/**
 * Exif tags (better to interpret than the tag IDs).
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public enum ExifTag {

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
     * Tag ID as specified in the EXIF standard
     */
    private final int tagId;

    /**
     * Returns the Tag ID.
     * 
     * @return tag ID
     */
    public int tagId() {
        return tagId;
    }

    /**
     * Returns a tag with an specific ID.
     * 
     * @param  tagId tag ID
     * @return       Tag or null if the ID is invalid
     */
    public static ExifTag getTag(int tagId) {
        for (ExifTag tag : ExifTag.values()) {
            if (tag.tagId == tagId) {
                return tag;
            }
        }
        return null;
    }

    private ExifTag(int tagId) {
        this.tagId = tagId;
    }
}
