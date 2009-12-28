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
package de.elmar_baumann.jpt.image.metadata.exif.format;

import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates instances of {@link ExifFormatter}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifFormatterFactory {

    private static final Map<Integer, ExifFormatter> FORMATTER_OF_TAG_ID = new HashMap<Integer, ExifFormatter>();

    static {
        // Ordered alphabetically for faster check
        FORMATTER_OF_TAG_ID.put(ExifTag.CONTRAST                  .tagId(), ExifFormatterContrast.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.COPYRIGHT                 .tagId(), ExifFormatterCopyright.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.DATE_TIME_ORIGINAL        .tagId(), ExifFormatterDateTime.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.EXPOSURE_PROGRAM          .tagId(), ExifFormatterExposureProgram.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.EXPOSURE_TIME             .tagId(), ExifFormatterExposureTime.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FILE_SOURCE               .tagId(), ExifFormatterFileSource.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FLASH                     .tagId(), ExifFormatterFlash.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FOCAL_LENGTH              .tagId(), ExifFormatterFocalLength.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.tagId(), ExifFormatterFocalLengthIn35mm.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.F_NUMBER                  .tagId(), ExifFormatterFnumber.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_DATE_STAMP            .tagId(), ExifFormatterGpsDateStamp.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_SATELLITES            .tagId(), ExifFormatterGpsSatellites.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_TIME_STAMP            .tagId(), ExifFormatterGpsTimeStamp.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.GPS_VERSION_ID            .tagId(), ExifFormatterGpsVersionId.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.IMAGE_UNIQUE_ID           .tagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.ISO_SPEED_RATINGS         .tagId(), ExifFormatterIsoSpeedRatings.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.METERING_MODE             .tagId(), ExifFormatterMeteringMode.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SATURATION                .tagId(), ExifFormatterSaturation.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SHARPNESS                 .tagId(), ExifFormatterSharpness.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.USER_COMMENT              .tagId(), ExifFormatterUserComment.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.WHITE_BALANCE             .tagId(), ExifFormatterWhiteBalance.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.ARTIST                    .tagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.IMAGE_DESCRIPTION         .tagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.MAKE                      .tagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.MODEL                     .tagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SOFTWARE                  .tagId(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.SPECTRAL_SENSITIVITY      .tagId(), ExifFormatterAscii.INSTANCE);
    }

    /**
     * Returns a formatter for a specific EXIF tag.
     *
     * @param  tagId ID of the exif tag
     * @return formatter or null if no formatter exists for that tag ID
     */
    public static ExifFormatter get(int tagId) {
        return FORMATTER_OF_TAG_ID.get(tagId);
    }

    private ExifFormatterFactory() {
    }
}
