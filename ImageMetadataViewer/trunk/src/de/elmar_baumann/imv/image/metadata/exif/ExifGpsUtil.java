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

    public static double toDouble(ExifRational rational) {
        double numerator = rational.getNumerator();
        double denominator = rational.getDenominator();
        assert denominator > 0 : denominator;
        return numerator / denominator;
    }

    public static long toLong(ExifRational rational) {
        return (long) Math.floor(toDouble(rational));
    }

    public static double getSeconds(ExifRational minutes) {
        double doubleMinutes = toDouble(minutes);
        double integerMinutes = toLong(minutes);
        return (doubleMinutes - integerMinutes) * 60;
    }

    public static String toString(ExifDegrees degrees) {
        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''"); // NOI18N
        double deg = ExifGpsUtil.toDouble(degrees.getDegrees());
        double min = ExifGpsUtil.toDouble(degrees.getMinutes());
        double sec = ExifGpsUtil.toDouble(degrees.getSeconds());
        if (sec == 0) {
            min = ExifGpsUtil.toLong(degrees.getMinutes());
            sec = ExifGpsUtil.getSeconds(degrees.getMinutes());
        }
        DecimalFormat dfDegMin = new DecimalFormat("#"); // NOI18N
        DecimalFormat dfSec = new DecimalFormat("#.##"); // NOI18N

        Object[] params = {dfDegMin.format(deg), dfDegMin.format(min), dfSec.format(sec)};
        return msg.format(params);
    }

    public static String getGoogleMapsUrl(ExifGpsLongitude longitude, ExifGpsLatitude latitude) {
        MessageFormat msg = new MessageFormat("http://maps.google.com/maps?q={0},{1}&spn=0.001,0.001&t=k&hl=de"); // NOI18N
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.########"); // NOI18N

        double latititudeValue = getDegrees(latitude.getDegrees());
        double longitudeValue = getDegrees(longitude.getDegrees());

        Object[] params = {df.format(latititudeValue), df.format(longitudeValue)};
        return msg.format(params);
    }

    public static double getDegrees(ExifDegrees degrees) {
        return toDouble(degrees.getDegrees()) +
            toDouble(degrees.getMinutes()) / 60 +
            toDouble(degrees.getSeconds()) / 3600;
    }

    private ExifGpsUtil() {
    }
}
