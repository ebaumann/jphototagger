package org.jphototagger.exif.formatter;

import org.jphototagger.exif.Ensure;
import org.jphototagger.exif.ExifTag;
import org.jphototagger.exif.datatype.ExifRational;
import org.jphototagger.exif.datatype.Fraction;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#EXPOSURE_TIME}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterExposureTime extends ExifFormatter {

    public static final ExifFormatterExposureTime INSTANCE = new ExifFormatterExposureTime();

    private ExifFormatterExposureTime() {
    }

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.EXPOSURE_TIME);

        if (ExifRational.getRawValueByteCount() == exifTag.getRawValue().length) {
            ExifRational time = new ExifRational(exifTag.getRawValue(), exifTag.convertByteOrderIdToByteOrder());
            Fraction fraction = getAsExposureTime(time);
            int numerator = fraction.getNumerator();
            int denominator = fraction.getDenominator();

            if (denominator > 1) {
                return Integer.toString(numerator) + " / " + Integer.toString(denominator) + " s";
            } else if (numerator > 1) {
                return Integer.toString(numerator) + " s";
            } else if (numerator / denominator == 1) {
                return "1 s";
            }
        }

        return "?";
    }

    private static Fraction getAsExposureTime(ExifRational er) {
        int numerator = er.getNumerator();
        int denominator = er.getDenominator();
        double result = (double) numerator / (double) denominator;

        if (result < 1) {
            return new Fraction(1, (int) ((double) denominator / (double) numerator + 0.5));
        } else if (result >= 1) {
            return new Fraction((int) ((double) numerator / (double) denominator + 0.5), 1);
        } else {
            return new Fraction(0, 0);
        }
    }
}
