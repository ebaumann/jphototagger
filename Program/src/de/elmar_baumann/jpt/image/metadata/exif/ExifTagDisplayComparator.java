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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the display order of EXIF data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifTagDisplayComparator implements Comparator<ExifTag> {

    private static final Map<Integer, Integer>    ORDER_OF_TAG_ID_VALUE = new HashMap<Integer, Integer>();
    private static final List<Integer>            TAG_ID_VALUES         = new ArrayList<Integer>(30);
    public static final  ExifTagDisplayComparator INSTANCE              = new ExifTagDisplayComparator();

    static {
        // display order
        TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME_ORIGINAL.value());
        TAG_ID_VALUES.add(ExifTag.Id.IMAGE_DESCRIPTION.value());
        TAG_ID_VALUES.add(ExifTag.Id.MAKE.value());
        TAG_ID_VALUES.add(ExifTag.Id.MODEL.value());
        TAG_ID_VALUES.add(ExifTag.Id.FOCAL_LENGTH.value());
        TAG_ID_VALUES.add(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.value());
        TAG_ID_VALUES.add(ExifTag.Id.SUBJECT_DISTANCE_RANGE.value());
        TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_TIME.value());
        TAG_ID_VALUES.add(ExifTag.Id.F_NUMBER.value());
        TAG_ID_VALUES.add(ExifTag.Id.ISO_SPEED_RATINGS.value());
        TAG_ID_VALUES.add(ExifTag.Id.METERING_MODE.value());
        TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_MODE.value());
        TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_PROGRAM.value());
        TAG_ID_VALUES.add(ExifTag.Id.FLASH.value());
        TAG_ID_VALUES.add(ExifTag.Id.WHITE_BALANCE.value());
        TAG_ID_VALUES.add(ExifTag.Id.SATURATION.value());
        TAG_ID_VALUES.add(ExifTag.Id.SHARPNESS.value());
        TAG_ID_VALUES.add(ExifTag.Id.CONTRAST.value());
        TAG_ID_VALUES.add(ExifTag.Id.USER_COMMENT.value());
        TAG_ID_VALUES.add(ExifTag.Id.COPYRIGHT.value());
        TAG_ID_VALUES.add(ExifTag.Id.ARTIST.value());
        TAG_ID_VALUES.add(ExifTag.Id.IMAGE_WIDTH.value());
        TAG_ID_VALUES.add(ExifTag.Id.IMAGE_LENGTH.value());
        TAG_ID_VALUES.add(ExifTag.Id.BITS_PER_SAMPLE.value());
        TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME_DIGITIZED.value());
        TAG_ID_VALUES.add(ExifTag.Id.FILE_SOURCE.value());
        TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME.value());
        TAG_ID_VALUES.add(ExifTag.Id.SOFTWARE.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_VERSION_ID.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_LATITUDE_REF.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_LATITUDE.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_LONGITUDE_REF.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_LONGITUDE.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_ALTITUDE_REF.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_ALTITUDE.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_TIME_STAMP.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_SATELLITES.value());
        TAG_ID_VALUES.add(ExifTag.Id.GPS_DATE_STAMP.value());

        int size = TAG_ID_VALUES.size();
        for (int i = 0; i < size; i++) {
            ORDER_OF_TAG_ID_VALUE.put(TAG_ID_VALUES.get(i), i);
        }
    }

    @Override
    public int compare(ExifTag exifTagLeft, ExifTag exifTagRight) {

        int tagLeft  = exifTagLeft.idValue();
        int tagRight = exifTagRight.idValue();

        if (  ORDER_OF_TAG_ID_VALUE.containsKey(tagLeft)
           && ORDER_OF_TAG_ID_VALUE.containsKey(tagRight)) {
            return ORDER_OF_TAG_ID_VALUE.get(tagLeft) - ORDER_OF_TAG_ID_VALUE.get(tagRight);
        }
        return tagLeft - tagRight;
    }

    private ExifTagDisplayComparator() {
    }
}
