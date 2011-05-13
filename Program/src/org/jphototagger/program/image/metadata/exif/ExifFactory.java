package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.lib.util.NumberUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Exif;
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

            ExifTag dateTimeOriginalTag = exifTags.exifTagById(ExifTag.Id.DATE_TIME_ORIGINAL.value());
            ExifTag focalLengthTag = exifTags.exifTagById(ExifTag.Id.FOCAL_LENGTH.value());
            ExifTag isoSpeedRatingsTag = exifTags.exifTagById(ExifTag.Id.ISO_SPEED_RATINGS.value());
            ExifTag modelTag = exifTags.exifTagById(ExifTag.Id.MODEL.value());
            ExifTag lensTag = exifTags.exifTagById(ExifTag.Id.MAKER_NOTE_LENS.value());

            if (dateTimeOriginalTag != null) {
                setExifDateTimeOriginal(exif, dateTimeOriginalTag);
            }

            if (focalLengthTag != null) {
                setExifFocalLength(exif, focalLengthTag);
            }

            if (isoSpeedRatingsTag != null) {
                setExifIsoSpeedRatings(exif, isoSpeedRatingsTag);
            }

            if (modelTag != null) {
                setExifEquipment(exif, modelTag);
            }

            if (lensTag != null) {
                exif.setLens(lensTag.stringValue());
            }

            return exif;
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        return null;
    }
    }

    private static void setExifDateTimeOriginal(Exif exif, ExifTag dateTimeOriginalTag) {
        String exifTagStringValue = dateTimeOriginalTag.stringValue();
        String dateTimeString = exifTagStringValue == null 
                                    ? "" 
                                    : exifTagStringValue.trim();
        int dateTimeStringLength = dateTimeString.length();

        if (dateTimeStringLength >= 11) {
            try {
                String yearString = dateTimeString.substring(0, 4);
                String monthString = dateTimeString.substring(5, 7);
                String dayString = dateTimeString.substring(8, 10);
                
                if (!NumberUtil.isInteger(yearString) || !NumberUtil.isInteger(monthString) || !NumberUtil.isInteger(dayString)) {
                    return;
                }
                
                int year = Integer.parseInt(yearString);
                int month = Integer.parseInt(monthString);
                int day = Integer.parseInt(dayString);
                Calendar calendar = new GregorianCalendar();

                if (year < 1839) {
                    AppLogger.logInfo(ExifFactory.class, "ExifFactory.Info.TooOldYear");
                    return;
                }

                calendar.set(year, month - 1, day);

                long timeInMillis = calendar.getTimeInMillis();
                Date dateTimeOriginal = new Date(timeInMillis);

                exif.setDateTimeOriginal(dateTimeOriginal);
                } catch (Exception ex) {
                    AppLogger.logSevere(ExifMetadata.class, ex);
                }
            }
        }

    private static void setExifEquipment(Exif exif, ExifTag modelTag) {
        String exifTagStringValue = modelTag.stringValue();

        if (exifTagStringValue != null) {
            exif.setRecordingEquipment(exifTagStringValue.trim());
        }
    }

    private static void setExifFocalLength(Exif exif, ExifTag focalLengthTag) {
        try {
            String exifTagStringValue = focalLengthTag.stringValue();
            StringTokenizer tokenizer = exifTagStringValue == null 
                                            ? new StringTokenizer("") 
                                            : new StringTokenizer(exifTagStringValue.trim(), "/:");

            if (tokenizer.countTokens() >= 1) {
                String denominatorString = tokenizer.nextToken();
                String numeratorString = null;

                if (tokenizer.hasMoreTokens()) {
                    numeratorString = tokenizer.nextToken();
                }

                if (!NumberUtil.isDouble(denominatorString)) {
                    return;
                }

                double denominator = Double.parseDouble(denominatorString);
                double focalLength = denominator;

                if (NumberUtil.isDouble(numeratorString)) {
                    double numerator = Double.parseDouble(numeratorString);

                    if (numerator != 0) {
                    focalLength = denominator / numerator;
                }
                }

                exif.setFocalLength(focalLength);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exif, ExifTag isoSpeedRatingsTag) {
        try {
            String exifTagStringValue = isoSpeedRatingsTag.stringValue();
            String isoSpeedRatingsString = exifTagStringValue == null 
                                               ? null 
                                               : exifTagStringValue.trim();
            
            if (NumberUtil.isShort(isoSpeedRatingsString)) {
                short isoSpeedRatings = Short.parseShort(isoSpeedRatingsString);
                
                exif.setIsoSpeedRatings(isoSpeedRatings);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(ExifMetadata.class, ex);
        }
    }

    private ExifFactory() {}
}
