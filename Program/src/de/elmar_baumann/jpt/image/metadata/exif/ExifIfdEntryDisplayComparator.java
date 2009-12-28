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
public final class ExifIfdEntryDisplayComparator implements Comparator<IfdEntryProxy> {

    private static final Map<Integer, Integer>        ORDER_OF_TAG_VALUE = new HashMap<Integer, Integer>();
    private static final List<Integer>                TAG_VALUES         = new ArrayList<Integer>(30);
    public static final ExifIfdEntryDisplayComparator INSTANCE           = new ExifIfdEntryDisplayComparator();

    static {
        // display order
        TAG_VALUES.add(ExifTag.DATE_TIME_ORIGINAL.tagId());
        TAG_VALUES.add(ExifTag.IMAGE_DESCRIPTION.tagId());
        TAG_VALUES.add(ExifTag.MAKE.tagId());
        TAG_VALUES.add(ExifTag.MODEL.tagId());
        TAG_VALUES.add(ExifTag.FOCAL_LENGTH.tagId());
        TAG_VALUES.add(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.tagId());
        TAG_VALUES.add(ExifTag.SUBJECT_DISTANCE_RANGE.tagId());
        TAG_VALUES.add(ExifTag.EXPOSURE_TIME.tagId());
        TAG_VALUES.add(ExifTag.F_NUMBER.tagId());
        TAG_VALUES.add(ExifTag.ISO_SPEED_RATINGS.tagId());
        TAG_VALUES.add(ExifTag.METERING_MODE.tagId());
        TAG_VALUES.add(ExifTag.EXPOSURE_MODE.tagId());
        TAG_VALUES.add(ExifTag.EXPOSURE_PROGRAM.tagId());
        TAG_VALUES.add(ExifTag.FLASH.tagId());
        TAG_VALUES.add(ExifTag.WHITE_BALANCE.tagId());
        TAG_VALUES.add(ExifTag.SATURATION.tagId());
        TAG_VALUES.add(ExifTag.SHARPNESS.tagId());
        TAG_VALUES.add(ExifTag.CONTRAST.tagId());
        TAG_VALUES.add(ExifTag.USER_COMMENT.tagId());
        TAG_VALUES.add(ExifTag.COPYRIGHT.tagId());
        TAG_VALUES.add(ExifTag.ARTIST.tagId());
        TAG_VALUES.add(ExifTag.IMAGE_WIDTH.tagId());
        TAG_VALUES.add(ExifTag.IMAGE_LENGTH.tagId());
        TAG_VALUES.add(ExifTag.BITS_PER_SAMPLE.tagId());
        TAG_VALUES.add(ExifTag.DATE_TIME_DIGITIZED.tagId());
        TAG_VALUES.add(ExifTag.FILE_SOURCE.tagId());
        TAG_VALUES.add(ExifTag.DATE_TIME.tagId());
        TAG_VALUES.add(ExifTag.SOFTWARE.tagId());
        TAG_VALUES.add(ExifTag.GPS_VERSION_ID.tagId());
        TAG_VALUES.add(ExifTag.GPS_LATITUDE_REF.tagId());
        TAG_VALUES.add(ExifTag.GPS_LATITUDE.tagId());
        TAG_VALUES.add(ExifTag.GPS_LONGITUDE_REF.tagId());
        TAG_VALUES.add(ExifTag.GPS_LONGITUDE.tagId());
        TAG_VALUES.add(ExifTag.GPS_ALTITUDE_REF.tagId());
        TAG_VALUES.add(ExifTag.GPS_ALTITUDE.tagId());
        TAG_VALUES.add(ExifTag.GPS_TIME_STAMP.tagId());
        TAG_VALUES.add(ExifTag.GPS_SATELLITES.tagId());
        TAG_VALUES.add(ExifTag.GPS_DATE_STAMP.tagId());

        int size = TAG_VALUES.size();
        for (int i = 0; i < size; i++) {
            ORDER_OF_TAG_VALUE.put(TAG_VALUES.get(i), i);
        }
    }

    @Override
    public int compare(IfdEntryProxy o1, IfdEntryProxy o2) {
        int tag1 = o1.tagId();
        int tag2 = o2.tagId();
        if (ORDER_OF_TAG_VALUE.containsKey(tag1) && ORDER_OF_TAG_VALUE.containsKey(tag2)) {
            return ORDER_OF_TAG_VALUE.get(tag1) - ORDER_OF_TAG_VALUE.get(tag2);
        }
        return tag1 - tag2;
    }

    private ExifIfdEntryDisplayComparator() {
    }
}
