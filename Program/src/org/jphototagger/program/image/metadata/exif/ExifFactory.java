package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Exif;

import java.io.File;

import java.sql.Date;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 *
 *
 * @author Elmar Baumann
 */
final class ExifFactory {

    /**
     * @return EXIF metadata or null
     */
    static Exif getExif(ExifTags exifTags) {
        if (exifTags == null) {
            return null;
        }

        try {
            Exif exif = new Exif();

            ExifTag dateTimeOriginal = exifTags.exifTagById(ExifTag.Id.DATE_TIME_ORIGINAL.value());
            ExifTag focalLength = exifTags.exifTagById(ExifTag.Id.FOCAL_LENGTH.value());
            ExifTag isoSpeedRatings = exifTags.exifTagById(ExifTag.Id.ISO_SPEED_RATINGS.value());
            ExifTag model = exifTags.exifTagById(ExifTag.Id.MODEL.value());
            ExifTag lens = exifTags.exifTagById(ExifTag.Id.MAKER_NOTE_LENS.value());

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
            AppLogger.logSevere(ExifMetadata.class, ex);
        }

        return null;
    }

    private static void setExifDateTimeOriginal(Exif exif, ExifTag exifTag) {
        String datestring = exifTag.stringValue();    // did throw a null pointer exception

        if ((datestring != null) && (datestring.trim().length() >= 11)) {
            try {
                int year = new Integer(datestring.substring(0, 4)).intValue();
                int month = new Integer(datestring.substring(5, 7)).intValue();
                int day = new Integer(datestring.substring(8, 10)).intValue();
                Calendar calendar = new GregorianCalendar();

                if (year < 1839) {
                    AppLogger.logInfo(ExifFactory.class, "ExifFactory.Info.TooOldYear");

                    return;
                }

                calendar.set(year, month - 1, day);

                try {
                    exif.setDateTimeOriginal(new Date(calendar.getTimeInMillis()));
                } catch (Exception ex) {
                    AppLogger.logSevere(ExifMetadata.class, ex);
                }
            } catch (Exception ex) {
                AppLogger.logSevere(ExifMetadata.class, ex);
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
            String length = exifTag.stringValue().trim();
            StringTokenizer tokenizer = new StringTokenizer(length, "/:");

            if (tokenizer.countTokens() >= 1) {
                String denominatorString = tokenizer.nextToken();
                String numeratorString = null;

                if (tokenizer.hasMoreTokens()) {
                    numeratorString = tokenizer.nextToken();
                }

                double denominator = Double.valueOf(denominatorString);
                double focalLength = denominator;

                if (numeratorString != null) {
                    double numerator = new Double(numeratorString);

                    focalLength = denominator / numerator;
                }

                exif.setFocalLength(focalLength);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exif, ExifTag exifTag) {
        try {
            exif.setIsoSpeedRatings(new Short(exifTag.stringValue().trim()).shortValue());
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }
    }

    private ExifFactory() {}
}
