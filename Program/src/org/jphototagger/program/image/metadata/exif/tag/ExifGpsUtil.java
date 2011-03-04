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
    public static double degrees(ExifDegrees degrees) {
        if (degrees == null) {
            throw new NullPointerException("degrees == null");
        }

        return ExifDatatypeUtil.toDouble(degrees.degrees()) + ExifDatatypeUtil.toDouble(degrees.minutes()) / 60
               + ExifDatatypeUtil.toDouble(degrees.seconds()) / 3600;
    }

    public static double secondsOfMinutes(ExifRational minutes) {
        if (minutes == null) {
            throw new NullPointerException("minutes == null");
        }

        double doubleMinutes = ExifDatatypeUtil.toDouble(minutes);
        double integerMinutes = ExifDatatypeUtil.toLong(minutes);

        return (doubleMinutes - integerMinutes) * 60;
    }

    public static String degreesToString(ExifDegrees degrees) {
        if (degrees == null) {
            throw new NullPointerException("degrees == null");
        }

        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''");
        double deg = ExifDatatypeUtil.toDouble(degrees.degrees());
        double min = ExifDatatypeUtil.toDouble(degrees.minutes());
        double sec = ExifDatatypeUtil.toDouble(degrees.seconds());

        if (sec == 0) {
            min = ExifDatatypeUtil.toLong(degrees.minutes());
            sec = ExifGpsUtil.secondsOfMinutes(degrees.minutes());
        }

        DecimalFormat dfDegMin = new DecimalFormat("#");
        DecimalFormat dfSec = new DecimalFormat("#.##");
        Object[] params = { dfDegMin.format(deg), dfDegMin.format(min), dfSec.format(sec) };

        return msg.format(params);
    }

    public static String googleMapsUrl(ExifGpsLongitude longitude, ExifGpsLatitude latitude) {
        if (longitude == null) {
            throw new NullPointerException("longitude == null");
        }

        if (latitude == null) {
            throw new NullPointerException("latitude == null");
        }

        MessageFormat msg = new MessageFormat("http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl="
                                + Locale.getDefault().getLanguage());
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

        df.applyPattern("#.########");

        double latititudeValue = degrees(latitude.degrees());
        double longitudeValue = degrees(longitude.degrees());

        if (latitude.ref().equals(ExifGpsLatitude.Ref.SOUTH)) {
            latititudeValue *= -1;
        }

        if (longitude.ref().equals(ExifGpsLongitude.Ref.WEST)) {
            longitudeValue *= -1;
        }

        Object[] params = { df.format(latititudeValue), df.format(longitudeValue) };

        return msg.format(params);
    }

    public static ExifGpsMetadata gpsMetadata(ExifTags exifTags) {
        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        ExifGpsMetadata gpsMetaData = new ExifGpsMetadata();

        setGpsLatitude(gpsMetaData, exifTags);
        setGpsLongitude(gpsMetaData, exifTags);
        setGpsAltitude(gpsMetaData, exifTags);
        setGpsDate(gpsMetaData, exifTags);
        setGpsTime(gpsMetaData, exifTags);

        return gpsMetaData;
    }

    /**
     * Returns the GPS date and time.
     *
     * @param  md meta data
     * @return    date and time or null if the meta data does not contain time
     *            information or on errors
     */
    public static Calendar getGpsTime(ExifGpsMetadata md) {
        if (md == null) {
            throw new NullPointerException("md == null");
        }

        ExifGpsDateStamp dateStamp = md.dateStamp();
        ExifGpsTimeStamp timeStamp = md.timeStamp();

        if ((dateStamp != null) && (timeStamp != null) && dateStamp.isValid()) {
            try {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                cal.set(Calendar.YEAR, dateStamp.getYear());
                cal.set(Calendar.MONTH, dateStamp.getMonth() - 1);
                cal.set(Calendar.DAY_OF_MONTH, dateStamp.getDay());
                cal.set(Calendar.HOUR_OF_DAY, (int) ExifDatatypeUtil.toDouble(timeStamp.hours()));
                cal.set(Calendar.MINUTE, (int) ExifDatatypeUtil.toDouble(timeStamp.minutes()));
                cal.set(Calendar.SECOND, (int) ExifDatatypeUtil.toDouble(timeStamp.seconds()));

                return cal;
            } catch (Exception ex) {
                AppLogger.logSevere(ExifGpsUtil.class, ex);
            }
        }

        return null;
    }

    private static void setGpsLongitude(ExifGpsMetadata gpsMetaData, ExifTags exifTags) {
        ExifTag tagLongitudeRef = exifTags.gpsTagById(ExifTag.Id.GPS_LONGITUDE_REF.value());
        ExifTag tagLongitude = exifTags.gpsTagById(ExifTag.Id.GPS_LONGITUDE.value());

        if ((tagLongitudeRef != null) && (tagLongitude != null)) {
            gpsMetaData.setLongitude(new ExifGpsLongitude(tagLongitudeRef.rawValue(), tagLongitude.rawValue(),
                    tagLongitude.byteOrder()));
        }
    }

    private static void setGpsLatitude(ExifGpsMetadata gpsMetaData, ExifTags exifTags) {
        ExifTag tagLatitudeRef = exifTags.gpsTagById(ExifTag.Id.GPS_LATITUDE_REF.value());
        ExifTag tagLatitude = exifTags.gpsTagById(ExifTag.Id.GPS_LATITUDE.value());

        if ((tagLatitudeRef != null) && (tagLatitude != null)) {
            gpsMetaData.setLatitude(new ExifGpsLatitude(tagLatitudeRef.rawValue(), tagLatitude.rawValue(),
                    tagLatitude.byteOrder()));
        }
    }

    private static void setGpsAltitude(ExifGpsMetadata gpsMetaData, ExifTags exifTags) {
        ExifTag tagAltitudeRef = exifTags.gpsTagById(ExifTag.Id.GPS_ALTITUDE_REF.value());
        ExifTag tagAltitude = exifTags.gpsTagById(ExifTag.Id.GPS_ALTITUDE.value());

        if ((tagAltitudeRef != null) && (tagAltitude != null)) {
            gpsMetaData.setAltitude(new ExifGpsAltitude(tagAltitudeRef.rawValue(), tagAltitude.rawValue(),
                    tagAltitude.byteOrder()));
        }
    }

    private static void setGpsDate(ExifGpsMetadata gpsMetaData, ExifTags exifTags) {
        ExifTag tagDate = exifTags.gpsTagById(ExifTag.Id.GPS_DATE_STAMP.value());

        if (tagDate != null) {
            byte[] rawValue = tagDate.rawValue();

            if ((rawValue != null) && (rawValue.length == 11)) {
                ExifGpsDateStamp dateStamp = new ExifGpsDateStamp(rawValue);

                gpsMetaData.setDateStamp(dateStamp);
            }
        }
    }

    private static void setGpsTime(ExifGpsMetadata gpsMetaData, ExifTags exifTags) {
        ExifTag tagTime = exifTags.gpsTagById(ExifTag.Id.GPS_TIME_STAMP.value());

        if (tagTime != null) {
            byte[] rawValue = tagTime.rawValue();

            if ((rawValue != null) && (rawValue.length == 24)) {
                ByteOrder byteOrder = tagTime.byteOrder();
                byte[] hoursRawValue = Arrays.copyOfRange(rawValue, 0, 8);
                byte[] minutesRawValue = Arrays.copyOfRange(rawValue, 8, 16);
                byte[] secondsRawValue = Arrays.copyOfRange(rawValue, 16, 24);
                ExifRational hours = new ExifRational(hoursRawValue, byteOrder);
                ExifRational minutes = new ExifRational(minutesRawValue, byteOrder);
                ExifRational seconds = new ExifRational(secondsRawValue, byteOrder);
                ExifGpsTimeStamp timeStamp = new ExifGpsTimeStamp(hours, minutes, seconds);

                gpsMetaData.setTimeStamp(timeStamp);
            }
        }
    }

    private ExifGpsUtil() {}
}
