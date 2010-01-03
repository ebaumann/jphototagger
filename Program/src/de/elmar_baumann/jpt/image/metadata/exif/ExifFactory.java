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

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Exif;
import java.io.File;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-30
 */
final class ExifFactory {

    /**
     * Returns EXIF metadata of a image file.
     *
     * @param  imageFile image file
     * @return           EXIF metadata or null if errors occured
     */
    static Exif getExif(File imageFile) {

        Exif     exif     = new Exif();
        ExifTags exifTags = ExifMetadata.getExifTags(imageFile);

        if (exifTags == null) return null;

        try {

            ExifTag dateTimeOriginal = exifTags.exifTagById(ExifTag.Id.DATE_TIME_ORIGINAL.value());
            ExifTag focalLength      = exifTags.exifTagById(ExifTag.Id.FOCAL_LENGTH.value());
            ExifTag isoSpeedRatings  = exifTags.exifTagById(ExifTag.Id.ISO_SPEED_RATINGS.value());
            ExifTag model            = exifTags.exifTagById(ExifTag.Id.MODEL.value());
            ExifTag lens             = exifTags.exifTagById(ExifTag.Id.MAKER_NOTE_LENS.value());

            if (dateTimeOriginal != null) {
                setExifDateTimeOriginal(exif, dateTimeOriginal);
            }

            if (focalLength != null) {
                setExifFocalLength(exif, focalLength);
            }

            if (isoSpeedRatings != null) {
                setExifIsoSpeedRatings(exif, isoSpeedRatings);
            }

            setExifEquipment(exif, model);

            if (lens != null) {
                exif.setLens(lens.stringValue());
            }

            return exif;

        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
        return null;
    }

    private static void setExifDateTimeOriginal(Exif exif, ExifTag exifTag) {

        String datestring = exifTag.stringValue(); // did throw a null pointer exception

        if (datestring != null && datestring.trim().length() >= 11) {
            try {
                int      year     = new Integer(datestring.substring(0, 4)).intValue();
                int      month    = new Integer(datestring.substring(5, 7)).intValue();
                int      day      = new Integer(datestring.substring(8, 10)).intValue();
                Calendar calendar = new GregorianCalendar();

                calendar.set(year, month - 1, day);
                try {
                    exif.setDateTimeOriginal(new Date(calendar.getTimeInMillis()));
                } catch (Exception ex) {
                    AppLog.logSevere(ExifMetadata.class, ex);
                }
            } catch (NumberFormatException ex) {
                AppLog.logSevere(ExifMetadata.class, ex);
            }
        }
    }

    private static void setExifEquipment(Exif exif, ExifTag exifTag) {
        if (exifTag != null) {
            exif.setRecordingEquipment(exifTag.stringValue().trim());
        }
    }

    private static void setExifFocalLength(Exif exif, ExifTag exifTag) {
        try {
            String          length    = exifTag.stringValue().trim();
            StringTokenizer tokenizer = new StringTokenizer(length, "/:");

            if (tokenizer.countTokens() >= 1) {
                String denominatorString = tokenizer.nextToken();
                String numeratorString   = null;
                if (tokenizer.hasMoreTokens()) {
                    numeratorString = tokenizer.nextToken();
                }
                double denominator = Double.valueOf(denominatorString);
                double focalLength = denominator;
                if (numeratorString != null) {
                    double numerator = new Double(numeratorString);
                    focalLength      = denominator / numerator;
                }
                exif.setFocalLength(focalLength);
            }
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exif, ExifTag exifTag) {
        try {
            exif.setIsoSpeedRatings(new Short(exifTag.stringValue().trim()).shortValue());
        } catch (Exception ex) {
            AppLog.logSevere(ExifMetadata.class, ex);
        }
    }

    private ExifFactory() {
    }

}
