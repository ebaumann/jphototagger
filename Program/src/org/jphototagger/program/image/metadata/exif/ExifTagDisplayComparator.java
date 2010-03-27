/*
 * @(#)ExifTagDisplayComparator.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.image.metadata.exif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the display order of EXIF data.
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class ExifTagDisplayComparator implements Comparator<ExifTag> {
    private static final Map<Integer, Integer> ORDER_OF_TAG_ID_VALUE =
        new HashMap<Integer, Integer>();
    private static final List<Integer> EXIF_IFD_TAG_ID_VALUES =
        new ArrayList<Integer>(30);
    private static final List<Integer> GPS_IFD_TAG_ID_VALUES =
        new ArrayList<Integer>(30);
    public static final ExifTagDisplayComparator INSTANCE =
        new ExifTagDisplayComparator();

    static {

        // display order
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME_ORIGINAL.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.IMAGE_DESCRIPTION.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.MAKE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.MODEL.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FOCAL_LENGTH.value());
        EXIF_IFD_TAG_ID_VALUES.add(
            ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SUBJECT_DISTANCE_RANGE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_TIME.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.F_NUMBER.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.ISO_SPEED_RATINGS.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.METERING_MODE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_MODE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_PROGRAM.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FLASH.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.WHITE_BALANCE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SATURATION.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SHARPNESS.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.CONTRAST.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.USER_COMMENT.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.COPYRIGHT.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.ARTIST.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.IMAGE_WIDTH.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.IMAGE_LENGTH.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.BITS_PER_SAMPLE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME_DIGITIZED.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FILE_SOURCE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SOFTWARE.value());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.MAKER_NOTE_LENS.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_VERSION_ID.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LATITUDE_REF.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LATITUDE.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LONGITUDE_REF.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LONGITUDE.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_ALTITUDE_REF.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_ALTITUDE.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_TIME_STAMP.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_SATELLITES.value());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_DATE_STAMP.value());

        int order = 0;

        // 1. EXIF IFD
        int size = EXIF_IFD_TAG_ID_VALUES.size();

        for (int index = 0; index < size; index++) {
            ORDER_OF_TAG_ID_VALUE.put(EXIF_IFD_TAG_ID_VALUES.get(index),
                                      order++);
        }

        // 2. GPS IFD
        size = GPS_IFD_TAG_ID_VALUES.size();

        for (int index = 0; index < size; index++) {
            ORDER_OF_TAG_ID_VALUE.put(GPS_IFD_TAG_ID_VALUES.get(index),
                                      order++);
        }
    }

    @Override
    public int compare(ExifTag exifTagLeft, ExifTag exifTagRight) {
        int tagIdLeft  = exifTagLeft.idValue();
        int tagIdRight = exifTagRight.idValue();

        if (ORDER_OF_TAG_ID_VALUE.containsKey(tagIdLeft)
                && ORDER_OF_TAG_ID_VALUE.containsKey(tagIdRight)) {
            return ORDER_OF_TAG_ID_VALUE.get(tagIdLeft)
                   - ORDER_OF_TAG_ID_VALUE.get(tagIdRight);
        }

        return tagIdLeft - tagIdRight;
    }

    private ExifTagDisplayComparator() {}
}
