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
import java.util.List;

/**
 * Contains the EXIF metadata to display (show) to the user.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifMetadataToDisplay {

    private static final List<Integer> TAGS_TO_DISPLAY =
            new ArrayList<Integer>();

    static {
        // Ordered alphabetically
        TAGS_TO_DISPLAY.add(ExifTag.ARTIST.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.CONTRAST.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.COPYRIGHT.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.DATE_TIME_ORIGINAL.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.EXPOSURE_PROGRAM.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.EXPOSURE_TIME.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.FILE_SOURCE.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.FLASH.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.F_NUMBER.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.FOCAL_LENGTH.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_DATE_STAMP.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_SATELLITES.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_TIME_STAMP.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_VERSION_ID.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.IMAGE_DESCRIPTION.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.IMAGE_UNIQUE_ID.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.ISO_SPEED_RATINGS.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.MAKE.tagId());
        //TAGS_TO_DISPLAY.add(ExifTag.MAKER_NOTE.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.METERING_MODE.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.MODEL.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.SATURATION.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.SHARPNESS.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.SOFTWARE.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.USER_COMMENT.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.WHITE_BALANCE.tagId());
        TAGS_TO_DISPLAY.add(ExifTag.SPECTRAL_SENSITIVITY.tagId());
    }

    /**
     * Returns the EXIF metadata to display to the user.
     *
     * @param  entries metadata
     * @return         subset of entries (displayable metadata)
     */
    public static List<IfdEntryProxy> get(List<IfdEntryProxy> entries) {

        List<IfdEntryProxy> displayableEntries = new ArrayList<IfdEntryProxy>(entries.size());

        for (IfdEntryProxy entry : entries) {
            if (isTagToDisplay(entry.tagId())) {
                if (!contains(displayableEntries, entry)) {
                    displayableEntries.add(entry);
                }
            }
        }
        return displayableEntries;
    }

    private static boolean contains(List<IfdEntryProxy> entries, IfdEntryProxy entry) {
        for (IfdEntryProxy e : entries) {
            if (ExifIfdEntryComparator.INSTANCE.compare(e, entry) == 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTagToDisplay(int tag) {
        return TAGS_TO_DISPLAY.contains(tag);
    }

    private ExifMetadataToDisplay() {
    }
}
