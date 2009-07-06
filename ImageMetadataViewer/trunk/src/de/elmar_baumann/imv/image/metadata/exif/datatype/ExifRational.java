package de.elmar_baumann.imv.image.metadata.exif.datatype;

import java.util.Arrays;

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
     * @param  rawValue   raw value
     * @param  byteOrder  byte order
     * @throws IllegalArgumentException if the length of the raw value is not
     *         equals to {@link #getRawValueByteCount()} or if the result is
     *         negativ or if the denominator is zero
     */
    public ExifRational(byte[] rawValue, ExifByteOrder byteOrder) {

        if (!isRawValueByteCountOk(rawValue))
            throw new IllegalArgumentException(
                    "Illegal raw value byte count: " + rawValue.length);

        numerator = ExifDatatypeUtil.intFromRawValue(
                Arrays.copyOfRange(rawValue, 0, 4),
                byteOrder);
        denominator = ExifDatatypeUtil.intFromRawValue(
                Arrays.copyOfRange(rawValue, 4, 8), byteOrder);

        if (isNegativ())
            throw new IllegalArgumentException("Negativ expression: " +
                    numerator + "/" + denominator);
        if (denominator == 0)
            throw new IllegalArgumentException("Illegal denominator: " +
                    denominator);
    }

    private boolean isNegativ() {
        return numerator < 0 && denominator > 0 || numerator > 0 &&
                denominator < 0;
    }

    /**
     * Returns the valid raw value byte count.
     *
     * @return valid raw value byte count
     */
    public static int getRawValueByteCount() {
        return 8;
    }

    public static boolean isRawValueByteCountOk(byte[] rawValue) {
        return rawValue.length == getRawValueByteCount();
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

    public ExifDataType getDataTyp() {
        return ExifDataType.RATIONAL;
    }
}
