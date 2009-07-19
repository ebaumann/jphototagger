package de.elmar_baumann.imv.image.metadata.exif.entry;

import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifDatatypeUtil;
import de.elmar_baumann.imv.image.metadata.exif.datatype.ExifRational;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-03-31
 */
public final class ExifGpsUtil {

    public static double getDegrees(ExifDegrees degrees) {
        return ExifDatatypeUtil.toDouble(degrees.getDegrees()) +
                ExifDatatypeUtil.toDouble(degrees.getMinutes()) / 60 +
                ExifDatatypeUtil.toDouble(degrees.getSeconds()) / 3600;
    }

    public static double getSecondsOfMinutes(ExifRational minutes) {
        double doubleMinutes = ExifDatatypeUtil.toDouble(minutes);
        double integerMinutes = ExifDatatypeUtil.toLong(minutes);
        return (doubleMinutes - integerMinutes) * 60;
    }

    public static String degreesToString(ExifDegrees degrees) {
        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''"); // NOI18N
        double deg = ExifDatatypeUtil.toDouble(degrees.getDegrees());
        double min = ExifDatatypeUtil.toDouble(degrees.getMinutes());
        double sec = ExifDatatypeUtil.toDouble(degrees.getSeconds());
        if (sec == 0) {
            min = ExifDatatypeUtil.toLong(degrees.getMinutes());
            sec = ExifGpsUtil.getSecondsOfMinutes(degrees.getMinutes());
        }
        DecimalFormat dfDegMin = new DecimalFormat("#"); // NOI18N
        DecimalFormat dfSec = new DecimalFormat("#.##"); // NOI18N

        Object[] params = {dfDegMin.format(deg), dfDegMin.format(min), dfSec.
            format(sec)};
        return msg.format(params);
    }

    public static String getGoogleMapsUrl(ExifGpsLongitude longitude,
            ExifGpsLatitude latitude) {
        MessageFormat msg =
                new MessageFormat(
                "http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl=de"); // NOI18N
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(
                Locale.ENGLISH);
        df.applyPattern("#.########"); // NOI18N

        double latititudeValue = getDegrees(latitude.getDegrees());
        double longitudeValue = getDegrees(longitude.getDegrees());

        Object[] params = {df.format(latititudeValue), df.format(longitudeValue)};
        return msg.format(params);
    }

    public static ExifGpsMetadata getGpsMetadata(List<IdfEntryProxy> entries) {
        ExifGpsMetadata data = new ExifGpsMetadata();

        setGpsLatitude(data, entries);
        setGpsLongitude(data, entries);
        setGpsAltitude(data, entries);

        return data;
    }

    private static void setGpsAltitude(ExifGpsMetadata data,
            List<IdfEntryProxy> entries) {
        IdfEntryProxy entryAltitudeRef = ExifMetadata.findEntryWithTag(entries,
                ExifTag.GPS_ALTITUDE_REF.getId());
        IdfEntryProxy entryAltitude = ExifMetadata.findEntryWithTag(entries,
                ExifTag.GPS_ALTITUDE.getId());
        if (entryAltitudeRef != null && entryAltitude != null) {
            data.setAltitude(new ExifGpsAltitude(entryAltitudeRef.getRawValue(),
                    entryAltitude.getRawValue(), entryAltitude.getByteOrder()));
        }
    }

    private static void setGpsLatitude(ExifGpsMetadata data,
            List<IdfEntryProxy> entries) {
        IdfEntryProxy entryLatitudeRef = ExifMetadata.findEntryWithTag(entries,
                ExifTag.GPS_LATITUDE_REF.getId());
        IdfEntryProxy entryLatitude = ExifMetadata.findEntryWithTag(entries,
                ExifTag.GPS_LATITUDE.getId());
        if (entryLatitudeRef != null && entryLatitude != null) {
            data.setLatitude(new ExifGpsLatitude(entryLatitudeRef.getRawValue(),
                    entryLatitude.getRawValue(), entryLatitude.getByteOrder()));
        }
    }

    private static void setGpsLongitude(ExifGpsMetadata data,
            List<IdfEntryProxy> entries) {
        IdfEntryProxy entryLongitudeRef = ExifMetadata.findEntryWithTag(entries,
                ExifTag.GPS_LONGITUDE_REF.getId());
        IdfEntryProxy entryLongitude = ExifMetadata.findEntryWithTag(entries,
                ExifTag.GPS_LONGITUDE.getId());
        if (entryLongitudeRef != null && entryLongitude != null) {
            data.setLongitude(new ExifGpsLongitude(
                    entryLongitudeRef.getRawValue(),
                    entryLongitude.getRawValue(), entryLongitude.getByteOrder()));
        }
    }

    private ExifGpsUtil() {
    }
}
