package org.jphototagger.exif;

import java.sql.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.lib.util.NumberUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
final class ExifFactory {

    private static final Logger LOGGER = Logger.getLogger(ExifFactory.class.getName());

    /**
     * @return EXIF metadata or null
     */
    static Exif getExif(ExifTags exifTags) {
        if (exifTags == null) {
            return null;
        }

        try {
            Exif exif = new Exif();

            ExifTag dateTimeOriginalTag = exifTags.findExifTagByTagId(ExifTag.Id.DATE_TIME_ORIGINAL.getTagId());
            ExifTag focalLengthTag = exifTags.findExifTagByTagId(ExifTag.Id.FOCAL_LENGTH.getTagId());
            ExifTag isoSpeedRatingsTag = exifTags.findExifTagByTagId(ExifTag.Id.ISO_SPEED_RATINGS.getTagId());
            ExifTag modelTag = exifTags.findExifTagByTagId(ExifTag.Id.MODEL.getTagId());
            ExifTag lensTag = exifTags.findExifTagByTagId(ExifTag.Id.MAKER_NOTE_LENS.getTagId());

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
                exif.setLens(lensTag.getStringValue());
            }

            return exif;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static void setExifDateTimeOriginal(Exif exif, ExifTag dateTimeOriginalTag) {
        String exifTagStringValue = dateTimeOriginalTag.getStringValue();
        String dateTimeString = exifTagStringValue == null
                ? ""
                : exifTagStringValue.trim();
        int dateTimeStringLength = dateTimeString.length();

        if (dateTimeStringLength >= 19) {
            long timeInMillis = ExifMetadata.exifDateTimeStringToTimestamp(dateTimeString);
            Date dateTimeOriginal = new Date(timeInMillis < 0 ? 0 : timeInMillis);

            exif.setDateTimeOriginal(dateTimeOriginal);
            exif.setDateTimeOriginalTimestamp(timeInMillis);
        }
    }

    private static void setExifEquipment(Exif exif, ExifTag modelTag) {
        String exifTagStringValue = modelTag.getStringValue();

        if (exifTagStringValue != null) {
            exif.setRecordingEquipment(exifTagStringValue.trim());
        }
    }

    private static void setExifFocalLength(Exif exif, ExifTag focalLengthTag) {
        try {
            String exifTagStringValue = focalLengthTag.getStringValue();
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

                if (focalLength > 0) {
                    exif.setFocalLength(focalLength);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private static void setExifIsoSpeedRatings(Exif exif, ExifTag isoSpeedRatingsTag) {
        try {
            String exifTagStringValue = isoSpeedRatingsTag.getStringValue();
            String isoSpeedRatingsString = exifTagStringValue == null
                    ? null
                    : exifTagStringValue.trim();

            if (NumberUtil.isShort(isoSpeedRatingsString)) {
                short isoSpeedRatings = Short.parseShort(isoSpeedRatingsString);

                exif.setIsoSpeedRatings(isoSpeedRatings);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private ExifFactory() {
    }
}
