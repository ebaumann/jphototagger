package de.elmar_baumann.imv.image.metadata.exif;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/31
 */
public final class ExifGpsUtil {

    public static double getDegrees(ExifDegrees degrees) {
        return ExifUtil.toDouble(degrees.getDegrees()) +
                ExifUtil.toDouble(degrees.getMinutes()) / 60 +
                ExifUtil.toDouble(degrees.getSeconds()) / 3600;
    }

    public static double getSecondsOfMinutes(ExifRational minutes) {
        double doubleMinutes = ExifUtil.toDouble(minutes);
        double integerMinutes = ExifUtil.toLong(minutes);
        return (doubleMinutes - integerMinutes) * 60;
    }

    public static String degreesToString(ExifDegrees degrees) {
        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''"); // NOI18N
        double deg = ExifUtil.toDouble(degrees.getDegrees());
        double min = ExifUtil.toDouble(degrees.getMinutes());
        double sec = ExifUtil.toDouble(degrees.getSeconds());
        if (sec == 0) {
            min = ExifUtil.toLong(degrees.getMinutes());
            sec = ExifGpsUtil.getSecondsOfMinutes(degrees.getMinutes());
        }
        DecimalFormat dfDegMin = new DecimalFormat("#"); // NOI18N
        DecimalFormat dfSec = new DecimalFormat("#.##"); // NOI18N

        Object[] params = {dfDegMin.format(deg), dfDegMin.format(min), dfSec.format(sec)};
        return msg.format(params);
    }

    public static String getGoogleMapsUrl(ExifGpsLongitude longitude, ExifGpsLatitude latitude) {
        MessageFormat msg = new MessageFormat("http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl=de"); // NOI18N
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.applyPattern("#.########"); // NOI18N

        double latititudeValue = getDegrees(latitude.getDegrees());
        double longitudeValue = getDegrees(longitude.getDegrees());

        Object[] params = {df.format(latititudeValue), df.format(longitudeValue)};
        return msg.format(params);
    }

    private ExifGpsUtil() {
    }
}
