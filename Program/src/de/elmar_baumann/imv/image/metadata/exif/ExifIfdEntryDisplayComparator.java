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
package de.elmar_baumann.imv.image.metadata.exif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vergleicht IFDEntries zum Sortieren für die Anzeige.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifIfdEntryDisplayComparator implements
        Comparator<IdfEntryProxy> {

    private static final Map<Integer, Integer> ORDER_OF_TAG_VALUE =
            new HashMap<Integer, Integer>();
    private static final List<Integer> TAG_VALUES = new ArrayList<Integer>(30);
    public static final ExifIfdEntryDisplayComparator INSTANCE =
            new ExifIfdEntryDisplayComparator();

    static {
        // display order
        TAG_VALUES.add(ExifTag.DATE_TIME_ORIGINAL.getId());
        TAG_VALUES.add(ExifTag.IMAGE_DESCRIPTION.getId());
        TAG_VALUES.add(ExifTag.MAKE.getId());
        TAG_VALUES.add(ExifTag.MODEL.getId());
        TAG_VALUES.add(ExifTag.FOCAL_LENGTH.getId());
        TAG_VALUES.add(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId());
        TAG_VALUES.add(ExifTag.SUBJECT_DISTANCE_RANGE.getId());
        TAG_VALUES.add(ExifTag.EXPOSURE_TIME.getId());
        TAG_VALUES.add(ExifTag.F_NUMBER.getId());
        TAG_VALUES.add(ExifTag.ISO_SPEED_RATINGS.getId());
        TAG_VALUES.add(ExifTag.METERING_MODE.getId());
        TAG_VALUES.add(ExifTag.EXPOSURE_MODE.getId());
        TAG_VALUES.add(ExifTag.EXPOSURE_PROGRAM.getId());
        TAG_VALUES.add(ExifTag.FLASH.getId());
        TAG_VALUES.add(ExifTag.WHITE_BALANCE.getId());
        TAG_VALUES.add(ExifTag.SATURATION.getId());
        TAG_VALUES.add(ExifTag.SHARPNESS.getId());
        TAG_VALUES.add(ExifTag.CONTRAST.getId());
        TAG_VALUES.add(ExifTag.USER_COMMENT.getId());
        TAG_VALUES.add(ExifTag.COPYRIGHT.getId());
        TAG_VALUES.add(ExifTag.ARTIST.getId());
        TAG_VALUES.add(ExifTag.IMAGE_WIDTH.getId());
        TAG_VALUES.add(ExifTag.IMAGE_LENGTH.getId());
        TAG_VALUES.add(ExifTag.BITS_PER_SAMPLE.getId());
        TAG_VALUES.add(ExifTag.DATE_TIME_DIGITIZED.getId());
        TAG_VALUES.add(ExifTag.FILE_SOURCE.getId());
        TAG_VALUES.add(ExifTag.DATE_TIME.getId());
        TAG_VALUES.add(ExifTag.SOFTWARE.getId());
        TAG_VALUES.add(ExifTag.GPS_VERSION_ID.getId());
        TAG_VALUES.add(ExifTag.GPS_LATITUDE_REF.getId());
        TAG_VALUES.add(ExifTag.GPS_LATITUDE.getId());
        TAG_VALUES.add(ExifTag.GPS_LONGITUDE_REF.getId());
        TAG_VALUES.add(ExifTag.GPS_LONGITUDE.getId());
        TAG_VALUES.add(ExifTag.GPS_ALTITUDE_REF.getId());
        TAG_VALUES.add(ExifTag.GPS_ALTITUDE.getId());
        TAG_VALUES.add(ExifTag.GPS_TIME_STAMP.getId());
        TAG_VALUES.add(ExifTag.GPS_SATELLITES.getId());
        TAG_VALUES.add(ExifTag.GPS_DATE_STAMP.getId());

        int size = TAG_VALUES.size();
        for (int i = 0; i < size; i++) {
            ORDER_OF_TAG_VALUE.put(TAG_VALUES.get(i), i);
        }
    }

    @Override
    public int compare(IdfEntryProxy o1, IdfEntryProxy o2) {
        int tag1 = o1.getTag();
        int tag2 = o2.getTag();
        if (ORDER_OF_TAG_VALUE.containsKey(tag1) && ORDER_OF_TAG_VALUE.
                containsKey(
                tag2)) {
            return ORDER_OF_TAG_VALUE.get(tag1) - ORDER_OF_TAG_VALUE.get(tag2);
        }
        return tag1 - tag2;
    }

    private ExifIfdEntryDisplayComparator() {
    }
}
