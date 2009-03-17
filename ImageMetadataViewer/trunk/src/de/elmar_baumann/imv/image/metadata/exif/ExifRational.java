package de.elmar_baumann.imv.image.metadata.exif;

/**
 * Rational Data Type of Exif Metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifRational {

    private long numerator;
    private long denominator;

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
}
