package de.elmar_baumann.imv.image.metadata.exif;

import java.text.MessageFormat;

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
        MessageFormat msg = new MessageFormat("{0}° {1}'' {2}''''");
        double deg = ExifGpsUtil.toDouble(degrees.getDegrees());
        double min = ExifGpsUtil.toDouble(degrees.getMinutes());
        double sec = ExifGpsUtil.toDouble(degrees.getSeconds());
        if (sec == 0) {
            min = ExifGpsUtil.toLong(degrees.getMinutes());
            sec = ExifGpsUtil.getSeconds(degrees.getMinutes());
        }
        Object[] params = {deg, min, sec};
        return msg.format(params);
    }

    private ExifGpsUtil() {
    }
}
