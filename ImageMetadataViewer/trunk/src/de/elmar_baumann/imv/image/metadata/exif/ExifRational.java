package de.elmar_baumann.imv.image.metadata.exif;

import java.util.StringTokenizer;

/**
 * Rational Data Type of Exif Metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifRational {

    private long numerator = Long.MIN_VALUE;
    private long denominator = Long.MIN_VALUE;

    public ExifRational(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public long getDenominator() {
        return denominator;
    }

    public long getNumerator() {
        return numerator;
    }

    public static long getNumerator(byte[] rawValue) {
        long n = Long.MIN_VALUE;
        if (rawValue != null && rawValue.length == 1) {
            StringTokenizer tok = new StringTokenizer(new String(rawValue), "-/");
            if (tok.countTokens() == 2) {
                n = Long.getLong(tok.nextToken());
            }
        }
        return n;
    }

    public static long getDenominator(byte[] rawValue) {
        long n = Long.MIN_VALUE;
        if (rawValue != null && rawValue.length == 1) {
            StringTokenizer tok = new StringTokenizer(new String(rawValue), "-/");
            if (tok.countTokens() == 2) {
                tok.nextToken();
                n = Long.getLong(tok.nextToken());
            }
        }
        return n;
    }
}
