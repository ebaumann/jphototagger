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
package de.elmar_baumann.jpt.image.metadata.exif.formatter;

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
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.CONTRAST                  .value(), ExifFormatterContrast.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.COPYRIGHT                 .value(), ExifFormatterCopyright.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.DATE_TIME_ORIGINAL        .value(), ExifFormatterDateTime.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.EXPOSURE_PROGRAM          .value(), ExifFormatterExposureProgram.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.EXPOSURE_TIME             .value(), ExifFormatterExposureTime.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.FILE_SOURCE               .value(), ExifFormatterFileSource.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.FLASH                     .value(), ExifFormatterFlash.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.FOCAL_LENGTH              .value(), ExifFormatterFocalLength.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.value(), ExifFormatterFocalLengthIn35mm.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.F_NUMBER                  .value(), ExifFormatterFnumber.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.GPS_DATE_STAMP            .value(), ExifFormatterGpsDateStamp.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.GPS_SATELLITES            .value(), ExifFormatterGpsSatellites.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.GPS_TIME_STAMP            .value(), ExifFormatterGpsTimeStamp.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.GPS_VERSION_ID            .value(), ExifFormatterGpsVersionId.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.IMAGE_UNIQUE_ID           .value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.ISO_SPEED_RATINGS         .value(), ExifFormatterIsoSpeedRatings.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.METERING_MODE             .value(), ExifFormatterMeteringMode.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.SATURATION                .value(), ExifFormatterSaturation.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.SHARPNESS                 .value(), ExifFormatterSharpness.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.USER_COMMENT              .value(), ExifFormatterUserComment.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.WHITE_BALANCE             .value(), ExifFormatterWhiteBalance.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.ARTIST                    .value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.IMAGE_DESCRIPTION         .value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.MAKE                      .value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.MODEL                     .value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.SOFTWARE                  .value(), ExifFormatterAscii.INSTANCE);
        FORMATTER_OF_TAG_ID.put(ExifTag.Id.SPECTRAL_SENSITIVITY      .value(), ExifFormatterAscii.INSTANCE);
    }

    /**
     * Returns a formatter for a specific EXIF tag.
     *
     * @param  tagId     ID of the exif tag
     * @return formatter or null if no formatter exists for that tag ID
     */
    public static ExifFormatter get(int tagId) {
        return FORMATTER_OF_TAG_ID.get(tagId);
    }

    private ExifFormatterFactory() {
    }
}
