package org.jphototagger.program.image.metadata.exif.tag;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.metadata.exif.datatype.ExifDatatypeUtil;
import org.jphototagger.program.image.metadata.exif.datatype.ExifRational;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import org.jphototagger.program.image.metadata.exif.ExifTags;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifGpsUtil {

    public static double convertExifDegreesToDouble(ExifDegrees exifDegrees) {
        if (exifDegrees == null) {
            throw new NullPointerException("exifDegrees == null");
        }
        ExifRational degrees = exifDegrees.getDegrees();
        ExifRational minutes = exifDegrees.getMinutes();
        ExifRational seconds = exifDegrees.getSeconds();
        double degreesDouble = ExifDatatypeUtil.convertExifRationalToDouble(degrees);
        double minutesDouble = ExifDatatypeUtil.convertExifRationalToDouble(minutes);
        double secondsDouble = ExifDatatypeUtil.convertExifRationalToDouble(seconds);

        return degreesDouble + minutesDouble / 60 + secondsDouble / 3600;
    }

    public static double getSecondsOfMinutes(ExifRational minutes) {
        if (minutes == null) {
            throw new NullPointerException("minutes == null");
        }

        double doubleMinutes = ExifDatatypeUtil.convertExifRationalToDouble(minutes);
        double integerMinutes = ExifDatatypeUtil.convertExifRationalToLong(minutes);

        return (doubleMinutes - integerMinutes) * 60;
    }

    public static String getDegreesAsString(ExifDegrees exifDegrees) {
        if (exifDegrees == null) {
            throw new NullPointerException("exifDegrees == null");
        }

        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''");
        ExifRational exifDeg = exifDegrees.getDegrees();
        ExifRational exifMinutes = exifDegrees.getMinutes();
        ExifRational exifSeconds = exifDegrees.getSeconds();
        double degrees = ExifDatatypeUtil.convertExifRationalToDouble(exifDeg);
        double minutes = ExifDatatypeUtil.convertExifRationalToDouble(exifMinutes);
        double seconds = ExifDatatypeUtil.convertExifRationalToDouble(exifSeconds);

        if (seconds == 0) {
            minutes = ExifDatatypeUtil.convertExifRationalToLong(exifMinutes);
            seconds = ExifGpsUtil.getSecondsOfMinutes(exifMinutes);
        }

        DecimalFormat decmailFormatMinutes = new DecimalFormat("#");
        DecimalFormat decimalFormatSeconds = new DecimalFormat("#.##");
        String degreesFormatted = decmailFormatMinutes.format(degrees);
        String minutesFormatted = decmailFormatMinutes.format(minutes);
        String secondsFormatted = decimalFormatSeconds.format(seconds);
        Object[] params = { degreesFormatted, minutesFormatted, secondsFormatted};

        return msg.format(params);
    }

    public static String getGoogleMapsUrl(ExifGpsLongitude exifGpsLongitude, ExifGpsLatitude exifGpsLatitude) {
        if (exifGpsLongitude == null) {
            throw new NullPointerException("exifGpsLongitude == null");
        }

        if (exifGpsLatitude == null) {
            throw new NullPointerException("exifGpsLatitude == null");
        }

        String language = Locale.getDefault().getLanguage();

        MessageFormat msg = new MessageFormat("http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl=" + language);
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

        decimalFormat.applyPattern("#.########");

        double latititude = convertExifDegreesToDouble(exifGpsLatitude.getExifDegrees());
        double longitude = convertExifDegreesToDouble(exifGpsLongitude.getExifDegrees());
        ExifGpsLatitude.Ref latitudeRef = exifGpsLatitude.getRef();
        ExifGpsLongitude.Ref longitudeRef = exifGpsLongitude.getRef();
        boolean isSouth = latitudeRef.isSouth();
        boolean isWest = longitudeRef.isWest();

        if (isSouth) {
            latititude *= -1;
        }

        if (isWest) {
            longitude *= -1;
        }

        Object[] params = { decimalFormat.format(latititude), decimalFormat.format(longitude) };

        return msg.format(params);
    }

    public static ExifGpsMetadata createGpsMetadataFromExifTags(ExifTags exifTags) {
        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        ExifGpsMetadata exifGpsMetaData = new ExifGpsMetadata();

        setGpsLatitudeFromExifTagsToExifGpsMetadata(exifTags, exifGpsMetaData);
        setGpsLongitudeFromExifTagsToExifGpsMetadata(exifTags, exifGpsMetaData);
        setGpsAltitudeFromExifTagsToExifGpsMetadata(exifTags, exifGpsMetaData);
        setGpsDateFromExifTagsToExifGpsMetadata(exifTags, exifGpsMetaData);
        setGpsTimeFromExifTagsToExifGpsMetadata(exifTags, exifGpsMetaData);

        return exifGpsMetaData;
    }

    /**
     * Returns the GPS date and time.
     *
     * @param  exifGpsMetaData meta data
     * @return                 date and time or null if the meta data does not contain time information or on errors
     */
    public static Calendar getGpsTimeFromExifGpsMetadata(ExifGpsMetadata exifGpsMetaData) {
        if (exifGpsMetaData == null) {
            throw new NullPointerException("exifGpsMetaData == null");
        }

        ExifGpsDateStamp dateStamp = exifGpsMetaData.getGpsDateStamp();
        ExifGpsTimeStamp timeStamp = exifGpsMetaData.getTimeStamp();

        if ((dateStamp != null) && (timeStamp != null) && dateStamp.isValid()) {
            try {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                cal.set(Calendar.YEAR, dateStamp.getYear());
                cal.set(Calendar.MONTH, dateStamp.getMonth() - 1);
                cal.set(Calendar.DAY_OF_MONTH, dateStamp.getDay());
                cal.set(Calendar.HOUR_OF_DAY, (int) ExifDatatypeUtil.convertExifRationalToDouble(timeStamp.getHours()));
                cal.set(Calendar.MINUTE, (int) ExifDatatypeUtil.convertExifRationalToDouble(timeStamp.getMinutes()));
                cal.set(Calendar.SECOND, (int) ExifDatatypeUtil.convertExifRationalToDouble(timeStamp.getSeconds()));

                return cal;
            } catch (Exception ex) {
                AppLogger.logSevere(ExifGpsUtil.class, ex);
            }
        }

        return null;
    }

    private static void setGpsLongitudeFromExifTagsToExifGpsMetadata(ExifTags exifTags, ExifGpsMetadata exifGpsMetaData) {
        ExifTag tagLongitudeRef = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_LONGITUDE_REF.getTagId());
        ExifTag tagLongitude = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_LONGITUDE.getTagId());

        if ((tagLongitudeRef != null) && (tagLongitude != null)) {
            byte[] refRawValue = tagLongitudeRef.getRawValue();
            byte[] degreesRawValue = tagLongitude.getRawValue();
            ByteOrder byteOrder = tagLongitude.convertByteOrderIdToByteOrder();
            ExifGpsLongitude longitude = new ExifGpsLongitude(refRawValue, degreesRawValue, byteOrder);

            exifGpsMetaData.setLongitude(longitude);
        }
    }

    private static void setGpsLatitudeFromExifTagsToExifGpsMetadata(ExifTags exifTags, ExifGpsMetadata exifGpsMetaData) {
        ExifTag tagLatitudeRef = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_LATITUDE_REF.getTagId());
        ExifTag tagLatitude = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_LATITUDE.getTagId());

        if ((tagLatitudeRef != null) && (tagLatitude != null)) {
            byte[] refRawValue = tagLatitudeRef.getRawValue();
            byte[] degreesRawValue = tagLatitude.getRawValue();
            ByteOrder byteOrder = tagLatitude.convertByteOrderIdToByteOrder();
            ExifGpsLatitude latitude = new ExifGpsLatitude(refRawValue, degreesRawValue, byteOrder);

            exifGpsMetaData.setLatitude(latitude);
        }
    }

    private static void setGpsAltitudeFromExifTagsToExifGpsMetadata(ExifTags exifTags, ExifGpsMetadata exifGpsMetaData) {
        ExifTag tagAltitudeRef = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_ALTITUDE_REF.getTagId());
        ExifTag tagAltitude = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_ALTITUDE.getTagId());

        if ((tagAltitudeRef != null) && (tagAltitude != null)) {
            byte[] refRawValue = tagAltitudeRef.getRawValue();
            byte[] rawValue = tagAltitude.getRawValue();
            ByteOrder byteOrder = tagAltitude.convertByteOrderIdToByteOrder();
            ExifGpsAltitude altitude = new ExifGpsAltitude(refRawValue, rawValue, byteOrder);

            exifGpsMetaData.setAltitude(altitude);
        }
    }

    private static void setGpsDateFromExifTagsToExifGpsMetadata(ExifTags exifTags, ExifGpsMetadata exifGpsMetaData) {
        ExifTag tagDate = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_DATE_STAMP.getTagId());

        if (tagDate != null) {
            byte[] rawValue = tagDate.getRawValue();

            if ((rawValue != null) && (rawValue.length == 11)) {
                ExifGpsDateStamp dateStamp = new ExifGpsDateStamp(rawValue);

                exifGpsMetaData.setGpsDateStamp(dateStamp);
            }
        }
    }

    private static void setGpsTimeFromExifTagsToExifGpsMetadata(ExifTags exifTags, ExifGpsMetadata exifGpsMetaData) {
        ExifTag tagTime = exifTags.findGpsTagByTagId(ExifTag.Id.GPS_TIME_STAMP.getTagId());

        if (tagTime != null) {
            byte[] rawValue = tagTime.getRawValue();

            if ((rawValue != null) && (rawValue.length == 24)) {
                ByteOrder byteOrder = tagTime.convertByteOrderIdToByteOrder();
                byte[] hoursRawValue = Arrays.copyOfRange(rawValue, 0, 8);
                byte[] minutesRawValue = Arrays.copyOfRange(rawValue, 8, 16);
                byte[] secondsRawValue = Arrays.copyOfRange(rawValue, 16, 24);
                ExifRational hours = new ExifRational(hoursRawValue, byteOrder);
                ExifRational minutes = new ExifRational(minutesRawValue, byteOrder);
                ExifRational seconds = new ExifRational(secondsRawValue, byteOrder);
                ExifGpsTimeStamp timeStamp = new ExifGpsTimeStamp(hours, minutes, seconds);

                exifGpsMetaData.setTimeStamp(timeStamp);
            }
        }
    }

    private ExifGpsUtil() {}
}
