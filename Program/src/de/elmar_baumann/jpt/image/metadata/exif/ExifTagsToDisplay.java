/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.image.metadata.exif;

import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata.IfdType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains the EXIF metadata to display (show) to the user.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifTagsToDisplay {

    private static final List<Integer> ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY             = new ArrayList<Integer>();
    private static final List<Integer> ID_VALUES_OF_GPS_TAGS_TO_DISPLAY              = new ArrayList<Integer>();
    private static final List<Integer> ID_VALUES_OF_INTEROPARABILITY_TAGS_TO_DISPLAY = new ArrayList<Integer>();

    static {
        // Ordered alphabetically
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.ARTIST.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.CONTRAST.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.COPYRIGHT.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.DATE_TIME_ORIGINAL.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.EXPOSURE_PROGRAM.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.EXPOSURE_TIME.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FILE_SOURCE.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FLASH.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.F_NUMBER.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FOCAL_LENGTH.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.IMAGE_DESCRIPTION.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.IMAGE_UNIQUE_ID.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.ISO_SPEED_RATINGS.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.MAKE.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.METERING_MODE.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.MODEL.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SATURATION.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SHARPNESS.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SOFTWARE.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.USER_COMMENT.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.WHITE_BALANCE.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SPECTRAL_SENSITIVITY.value());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.MAKER_NOTE_LENS.value());

        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_DATE_STAMP.value());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_SATELLITES.value());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_TIME_STAMP.value());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_VERSION_ID.value());

    }

    /**
     * Returns the EXIF tags to display to the user.
     *
     * @param  exifTags EXIF tags
     * @return          subset of entries (displayable metadata)
     */
    public static List<ExifTag> get(Collection<ExifTag> exifTags) {

        List<ExifTag> displayableExifTags = new ArrayList<ExifTag>(exifTags.size());

        for (ExifTag exifTag : exifTags) {

            if (isTagToDisplay(exifTag)) {

                if (!contains(displayableExifTags, exifTag)) {

                    displayableExifTags.add(exifTag);
                }
            }
        }
        return displayableExifTags;
    }

    private static boolean contains(List<ExifTag> exifTags, ExifTag exifTag) {
        for (ExifTag e : exifTags) {
            if (e.equals(exifTag)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTagToDisplay(ExifTag exifTag) {

        IfdType ifdType = exifTag.ifdType();
        int     idValue = exifTag.idValue();

        assert IfdType.values().length == 5; // Handle added value!

        switch(ifdType) {
            case EXIF            : return ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.contains(idValue);
            case INTEROPERABILITY: return ID_VALUES_OF_INTEROPARABILITY_TAGS_TO_DISPLAY.contains(idValue);
            case GPS             : return ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.contains(idValue);
            default              : return ifdType.equals(IfdType.MAKER_NOTE);
        }
    }

    private ExifTagsToDisplay() {
    }
}
