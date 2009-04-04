package de.elmar_baumann.imv.image.metadata.exif;

/**
 * EXIF data type RATIONAL as described in the standard: Two LONGs. The first
 * LONG is the numerator and the second LONG expresses the denominator.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 * @see ExifLong
 */
public final class ExifRational {

    private final int numerator;
    private final int denominator;

    /**
     * Creates a new instance.
     *
     * @param  numeratorRawValue    raw value of the numerator
     * @param  denominatorRawValue  raw value of the denominator
     * @param  byteOrder            byte order
     * @throws IllegalArgumentException if the lengths of numerator or denominator
     *         are not equals to 4 or if the values are smaller than zero
     */
    public ExifRational(byte[] numeratorRawValue, byte[] denominatorRawValue,
        ExifMetadata.ByteOrder byteOrder) {

        if (numeratorRawValue.length != 4)
            throw new IllegalArgumentException("numeratorRawValue != 4: " + numeratorRawValue);
        if (denominatorRawValue.length != 4)
            throw new IllegalArgumentException("denominatorRawValue != 4: " + denominatorRawValue);

        numerator = ExifGpsUtil.intFromRawValue(numeratorRawValue, byteOrder);
        denominator = ExifGpsUtil.intFromRawValue(denominatorRawValue, byteOrder);

        if (numerator < 0)
            throw new IllegalArgumentException("numerator < 0: " + numerator);
        if (denominator < 0)
            throw new IllegalArgumentException("denominator < 0: " + denominator);
    }

    /**
     * Returns the denominator.
     *
     * @return denominator {@code >= 0}
     */
    public int getDenominator() {
        return denominator;
    }

    /**
     * Returns the numerator.
     *
     * @return numerator {@code >= 0}
     */
    public int getNumerator() {
        return numerator;
    }
}
